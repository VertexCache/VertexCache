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
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * A cache implementation that uses random eviction when capacity is exceeded.
 *
 * When the number of entries reaches the defined sizeCapacity, a random key is
 * selected from the cache and evicted to make room for the new entry. This strategy
 * is extremely simple and avoids the bookkeeping overhead of LRU, LFU, or CLOCK,
 * but does not guarantee retention of the most valuable data.
 *
 * Key characteristics:
 * - Eviction is triggered when the cache is full
 * - A random entry is removed to free space
 * - Suitable for scenarios where usage patterns are unpredictable
 *   or fairness among keys is acceptable
 *
 * Secondary indexes (idx1, idx2) and reverse indexing are supported,
 * ensuring proper cleanup of associated mappings upon eviction.
 */
public class CacheRandom<K, V> extends CacheBase<K, V> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final int sizeCapacity;
    private final Random random;

    public CacheRandom(int sizeCapacity) {
        this.sizeCapacity = sizeCapacity;
        this.setPrimaryCache(new ConcurrentHashMap<>());
        this.setSecondaryIndexOne(new ConcurrentHashMap<>());
        this.setSecondaryIndexTwo(new ConcurrentHashMap<>());
        this.random = new Random();
    }

    @Override
    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException {
        lock.writeLock().lock();
        try {
            if (this.getPrimaryCache().size() >= this.sizeCapacity) {
                evictRandom(); // eviction is now safe
            }

            this.putDefaultImpl(primaryKey, value, secondaryKeys);
        } catch (OutOfMemoryError e) {
            throw new VertexCacheTypeException("Out of memory, increase memory or use eviction policy other than none.");
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void evictRandom() {
        if (this.getPrimaryCache().isEmpty()) return;

        K[] keys = this.getPrimaryCache().keySet().toArray((K[]) new Object[0]);
        int randomIndex = random.nextInt(keys.length);
        K keyToRemove = keys[randomIndex];

        this.getPrimaryCache().remove(keyToRemove);
        this.cleanupIndexFor(keyToRemove);
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
            this.getPrimaryCache().remove(primaryKey);
            this.cleanupIndexFor(primaryKey);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
