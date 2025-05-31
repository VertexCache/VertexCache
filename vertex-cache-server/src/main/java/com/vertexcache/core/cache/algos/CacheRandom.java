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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A cache implementation that uses constant-time random eviction when capacity is exceeded.
 *
 * Maintains a parallel list of keys for fast indexed access, allowing true O(1) random eviction.
 * Also supports secondary indexes (idx1, idx2) and proper cleanup on removal.
 */
public class CacheRandom<K, V> extends CacheBase<K, V> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final int sizeCapacity;
    private final Random random;

    // Tracks keys for O(1) random access
    private final List<K> keyList;
    private final Map<K, Integer> keyIndexMap;

    public CacheRandom(int sizeCapacity) {
        this.sizeCapacity = sizeCapacity;
        this.setPrimaryCache(new ConcurrentHashMap<>());
        this.setSecondaryIndexOne(new ConcurrentHashMap<>());
        this.setSecondaryIndexTwo(new ConcurrentHashMap<>());
        this.random = new Random();

        this.keyList = new ArrayList<>(sizeCapacity);
        this.keyIndexMap = new HashMap<>(sizeCapacity);
    }

    @Override
    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException {
        lock.writeLock().lock();
        try {
            if (!this.getPrimaryCache().containsKey(primaryKey)) {
                if (this.getPrimaryCache().size() >= this.sizeCapacity) {
                    evictRandom();
                }
                // Only track new keys
                keyList.add(primaryKey);
                keyIndexMap.put(primaryKey, keyList.size() - 1);
            }

            this.putDefaultImpl(primaryKey, value, secondaryKeys);
        } catch (OutOfMemoryError e) {
            throw new VertexCacheTypeException("Out of memory, increase memory or use eviction policy other than none.");
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void evictRandom() {
        if (keyList.isEmpty()) return;

        int index = random.nextInt(keyList.size());
        K keyToRemove = keyList.get(index);

        // Remove from map and indexes
        this.getPrimaryCache().remove(keyToRemove);
        this.cleanupIndexFor(keyToRemove);

        // Swap with last element in list and pop
        int lastIndex = keyList.size() - 1;
        if (index != lastIndex) {
            K lastKey = keyList.get(lastIndex);
            keyList.set(index, lastKey);
            keyIndexMap.put(lastKey, index);
        }

        keyList.remove(lastIndex);
        keyIndexMap.remove(keyToRemove);
    }

    @Override
    public V get(K primaryKey) {
        lock.readLock().lock();
        try {
            return this.getDefaultImpl(primaryKey);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void remove(K primaryKey) {
        lock.writeLock().lock();
        try {
            if (this.getPrimaryCache().remove(primaryKey) != null) {
                this.cleanupIndexFor(primaryKey);

                Integer index = keyIndexMap.remove(primaryKey);
                if (index != null && index < keyList.size()) {
                    int lastIndex = keyList.size() - 1;
                    if (index != lastIndex) {
                        K lastKey = keyList.get(lastIndex);
                        keyList.set(index, lastKey);
                        keyIndexMap.put(lastKey, index);
                    }
                    keyList.remove(lastIndex);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
