/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.core.cache.algos;

import com.vertexcache.core.cache.CacheBase;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * TinyLFU (Tiny Least Frequently Used) is a sophisticated caching algorithm designed to maintain a high cache hit
 * ratio with minimal memory overhead. It combines the strengths of LFU (Least Frequently Used) and LRU (Least Recently
 * Used) policies using a probabilistic frequency counter (CountMinSketch) to manage the frequency of access and a small
 * LRU cache to manage recency.
 *
 * Components and Workflow
 * -----------------------
 * The TinyLFU algorithm in the provided code uses the following key components:
 *
 *  Primary Cache (LRU and LFU Combined):
 *   - lruCache: A small LRU cache to manage recently accessed items.
 *   - lfuCache: A larger LFU cache to store frequently accessed items.
 *   - lruQueue: A deque to manage the order of items in the LRU cache.
 *
 *  Frequency Estimation:
 *    CountMinSketch, A probabilistic data structure used to estimate the frequency of access
 *    for each item.
 *
 *  Synchronization:
 *    ReentrantReadWriteLock: Ensures thread safety during cache operations.
 *
 * Workflow
 * --------
 *  Insertion (put method):
 *
 *   If the item is already in the LRU cache, it is updated.
 *   If the item is in the LFU cache, it is updated and its frequency is incremented in the CountMinSketch.
 *   If the item is new:
 *     - If the LRU cache is full, the least recently used item is removed from the LRU cache and added to the LFU cache (promoted).
 *     - If the total size of the LRU and LFU caches exceeds the sizeCapacity, the least frequently used item in the LFU cache is evicted.
 *     _ The new item is added to the LRU cache and the LRU queue.
 *
 *  Access (get method):
 *   If the item is in the LRU cache, it is returned directly.
 *   If the item is in the LFU cache, it is promoted to the LRU cache (to capture recency), its frequency is incremented in the CountMinSketch, and it is returned.
 *   If the item is not found in either cache, null is returned.
 *
 *  Eviction (evict method):
 *    The least frequently used item in the LFU cache is identified and removed if the combined size of the LRU and LFU
 *    caches exceeds sizeCapacity.
 *
 * Promotion (promoteToLRU method):
 *    When an item in the LFU cache is accessed, it is promoted to the LRU cache. If the LRU cache is full, the least
 *    recently used item is demoted to the LFU cache.
 *
 */
import java.util.*;

public class CacheTinyLFU<K, V> extends CacheBase<K, V> {

    private int sizeCapacity;
    private final Map<K, V> lruCache;
    private final Map<K, V> lfuCache;
    private final CountMinSketch<K> frequencySketch;
    private final Deque<K> lruQueue;
    private final ReentrantReadWriteLock lock;

    public CacheTinyLFU(int sizeCapacity) {
        this.sizeCapacity = sizeCapacity;
        this.lruCache = new LinkedHashMap<>(sizeCapacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > sizeCapacity;
            }
        };
        this.lfuCache = new ConcurrentHashMap<>();
        this.frequencySketch = new CountMinSketch<>(4, 1000); // 4 hash functions, 1000 buckets
        this.lruQueue = new LinkedList<>();
        this.lock = new ReentrantReadWriteLock();
        this.setSecondaryIndexOne(new ConcurrentHashMap<>());
        this.setSecondaryIndexTwo(new ConcurrentHashMap<>());
    }

    @Override
    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException {
        try {
            if (lruCache.containsKey(primaryKey) || lfuCache.containsKey(primaryKey)) {
                return; // already cached
            }

            // Evict from LRU segment if full
            if (lruCache.size() >= sizeCapacity) {
                evict();
            }

            // Evict from LFU segment if full
            if (lfuCache.size() >= sizeCapacity) {
                K victim = selectLFUEvictionCandidate();
                if (victim != null) {
                    lfuCache.remove(victim);
                    cleanupIndexFor(victim);
                }
            }

            // Insert into LRU segment
            lruCache.put(primaryKey, value);
            lruQueue.addLast(primaryKey);

            // Frequency sketch update
            frequencySketch.add(primaryKey);

            // Store in primary cache
            getPrimaryCache().put(primaryKey, value);

            // Indexing
            updateSecondaryKeys(primaryKey, secondaryKeys);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new VertexCacheTypeException("FrequencySketch error: possible hash calculation issue. Check hash function and sketch size.", e);
        }
    }

    @Override
    public V get(K key) {
        lock.readLock().lock();
        try {
            V value = lruCache.get(key);
            if (value != null) {
                return value;
            }
            value = lfuCache.get(key);
            if (value != null) {
                frequencySketch.add(key);
                promoteToLRU(key, value);
            }
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void remove(K key) {
        lock.writeLock().lock();
        try {
            lruCache.remove(key);
            lruQueue.remove(key);
            lfuCache.remove(key);
            getPrimaryCache().remove(key);
            cleanupIndexFor(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private K selectLFUEvictionCandidate() {
        K victim = null;
        long minFrequency = Long.MAX_VALUE;

        for (K key : lfuCache.keySet()) {
            long freq = frequencySketch.estimateCount(key);
            if (freq < minFrequency) {
                minFrequency = freq;
                victim = key;
            }
        }

        return victim;
    }

    private void evict() {
        if (lruQueue.isEmpty()) {
            return;
        }
        K keyToRemove = lruQueue.removeFirst();
        lruCache.remove(keyToRemove);
        this.cleanupIndexFor(keyToRemove);
    }

    private void promoteToLRU(K key, V value) {
        lock.writeLock().lock();
        try {
            if (lruCache.size() >= sizeCapacity) {
                K evictedKey = lruQueue.pollFirst();
                if (evictedKey != null) {
                    if (lfuCache.size() + lruCache.size() >= sizeCapacity) {
                        evict();
                    }
                    lfuCache.put(evictedKey, lruCache.get(evictedKey));
                    lruCache.remove(evictedKey);
                }
            }
            lruCache.put(key, value);
            lruQueue.addLast(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Simplified CountMinSketch implementation for frequency estimation
    private static class CountMinSketch<K> {
        private final int depth;
        private final int width;
        private final int[][] table;
        private final int[] hashSeeds;

        public CountMinSketch(int depth, int width) {
            this.depth = depth;
            this.width = width;
            this.table = new int[depth][width];
            this.hashSeeds = new int[depth];
            Random rand = new Random();
            for (int i = 0; i < depth; i++) {
                hashSeeds[i] = rand.nextInt();
            }
        }

        private int hash(K key, int seed) {
            return Math.floorMod(key.hashCode() ^ seed, width);
        }

        public void add(K key) {
            for (int i = 0; i < depth; i++) {
                int index = hash(key, hashSeeds[i]);
                table[i][index]++;
            }
        }

        public int frequency(K key) {
            int minFreq = Integer.MAX_VALUE;
            for (int i = 0; i < depth; i++) {
                int index = hash(key, hashSeeds[i]);
                minFreq = Math.min(minFreq, table[i][index]);
            }
            return minFreq;
        }

        public int estimateCount(K key) {
            return frequency(key);
        }
    }
}
