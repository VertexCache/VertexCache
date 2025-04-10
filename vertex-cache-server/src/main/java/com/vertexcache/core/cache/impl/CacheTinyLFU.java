package com.vertexcache.core.cache.impl;

import com.vertexcache.core.exception.VertexCacheTypeException;
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
        lock.writeLock().lock();
        try {
            if (lruCache.containsKey(primaryKey)) {
                lruCache.put(primaryKey, value);
            } else if (lfuCache.containsKey(primaryKey)) {
                lfuCache.put(primaryKey, value);
                frequencySketch.add(primaryKey);
            } else {
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
                lruCache.put(primaryKey, value);
                lruQueue.addLast(primaryKey);
            }
            updateSecondaryKeys(primaryKey, secondaryKeys);
        } finally {
            lock.writeLock().unlock();
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
            lfuCache.remove(key);
            lruQueue.remove(key);
            removeSecondaryKeys(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void evict() {
        lock.writeLock().lock();
        try {
            K keyToRemove = null;
            int minFreq = Integer.MAX_VALUE;
            for (K key : lfuCache.keySet()) {
                int freq = frequencySketch.frequency(key);
                if (freq < minFreq) {
                    minFreq = freq;
                    keyToRemove = key;
                }
            }
            if (keyToRemove != null) {
                lfuCache.remove(keyToRemove);
                removeSecondaryKeys(keyToRemove);
            }
        } finally {
            lock.writeLock().unlock();
        }
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

    private void removeSecondaryKeys(K key) {
        for (Map.Entry<Object, K> entry : getSecondaryIndexOne().entrySet()) {
            if (entry.getValue().equals(key)) {
                getSecondaryIndexOne().remove(entry.getKey());
            }
        }
        for (Map.Entry<Object, K> entry : getSecondaryIndexTwo().entrySet()) {
            if (entry.getValue().equals(key)) {
                getSecondaryIndexTwo().remove(entry.getKey());
            }
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
            return (key.hashCode() ^ seed) % width;
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
    }
}
