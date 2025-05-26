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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * ARC (Adaptive Replacement Cache) is a cache replacement algorithm designed to adapt dynamically to changing access
 * patterns to improve cache hit rates and overall performance. It combines the benefits of both LRU
 * (Least Recently Used) and LFU (Least Frequently Used) cache eviction policies.
 *
 * Cache Structure:
 *  - primaryCache: Represents the main cache where frequently accessed items are stored.
 *  - ghostCache: Acts as a secondary cache for items that were recently accessed but not frequently accessed.
 *  - mainEvict: Contains items that were evicted from the main cache.
 *  - ghostEvict: Holds items that were evicted from the ghost cache.
 *
 * Adaptation:
 *   ARC dynamically adjusts the sizes of mainCache and ghostCache based on recent access patterns to optimize cache
 *   performance. It aims to strike a balance between storing frequently accessed items in primaryCache and retaining
 *   recently accessed items in ghostCache.
 *
 * New Item Insertion (PUT):
 *    When a new item is inserted into the cache, ARC first checks if it already exists in either primaryCache or
 *    ghostCache. If found, no further action is taken. If the cache is full (mainCache + ghostCache size exceeds the
 *    capacity), ARC initiates a replacement process to evict an item from either mainCache or ghostCache based on
 *    predefined rules.
 *
 * Cache Hit (GET):
 *    When an item is accessed from the cache (GET operation), ARC first checks if the item exists in either
 *    primaryCache or ghostCache. If found in mainCache, the item is promoted to ghostCache to indicate recent use.
 *    If found in ghostCache, its access frequency is increased, but it remains in ghostCache to maintain its position
 *    as a recently used item.
 *
 * Replacement (REPLACE):
 *   When the cache is full and a replacement is required, ARC selects a victim item from either mainEvict or
 *   ghostEvict based on predefined rules. The selection criteria prioritize evicting items that are less likely to be
 *   accessed again soon, based on both recency and frequency of access.
 *
 */
public class CacheARC<K, V> extends CacheBase<K, V> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Map<K, V> ghostCache = Collections.synchronizedMap(new ConcurrentHashMap<K, V>());
    private Map<K, V> mainEvict = Collections.synchronizedMap(new ConcurrentHashMap<K, V>());
    private Map<K, V> ghostEvict = Collections.synchronizedMap(new ConcurrentHashMap<K, V>());

    private int sizeCapacity;

    public CacheARC(int sizeCapacity) {
        this.sizeCapacity = sizeCapacity;
        this.setPrimaryCache(Collections.synchronizedMap(new ConcurrentHashMap<K, V>()));
        this.setSecondaryIndexOne(Collections.synchronizedMap(new ConcurrentHashMap<Object, K>()));
        this.setSecondaryIndexTwo(Collections.synchronizedMap(new ConcurrentHashMap<Object, K>()));
    }

    @Override
    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException {
        lock.writeLock().lock();
        try {
            if (this.getPrimaryCache().containsKey(primaryKey) || ghostCache.containsKey(primaryKey)) {
                return;
            }

            if (this.getPrimaryCache().size() + ghostCache.size() >= sizeCapacity) {
                if (this.getPrimaryCache().size() < sizeCapacity) {
                    replace();
                } else {
                    if (!this.getPrimaryCache().isEmpty()) {
                        K keyToEvict = this.getPrimaryCache().keySet().iterator().next();
                        ghostEvict.put(keyToEvict, this.getPrimaryCache().remove(keyToEvict));
                        this.cleanupIndexFor(keyToEvict);
                    } else {
                        K ghostKey = ghostCache.keySet().iterator().next();
                        ghostEvict.put(ghostKey, ghostCache.remove(ghostKey));
                    }
                }
            }

            this.getPrimaryCache().put(primaryKey, value);
            this.updateSecondaryKeys(primaryKey, secondaryKeys);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V get(K key) {
        lock.writeLock().lock(); // needed because we update ghostCache
        try {
            if (this.getPrimaryCache().containsKey(key)) {
                return this.getPrimaryCache().get(key);
            } else if (ghostCache.containsKey(key)) {
                ghostEvict.put(key, ghostCache.remove(key)); // touch for recency
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void remove(K key) {
        lock.writeLock().lock();
        try {
            this.getPrimaryCache().remove(key);
            ghostCache.remove(key);
            mainEvict.remove(key);
            ghostEvict.remove(key);
            this.cleanupIndexFor(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void replace() {
        lock.writeLock().lock();
        try {
            if (!mainEvict.isEmpty()) {
                K key = mainEvict.keySet().iterator().next();
                mainEvict.remove(key);
                this.getPrimaryCache().put(key, null);
            } else if (!ghostCache.isEmpty()) {
                K key = ghostEvict.keySet().iterator().next();
                ghostEvict.remove(key);
                ghostCache.put(key, null);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
