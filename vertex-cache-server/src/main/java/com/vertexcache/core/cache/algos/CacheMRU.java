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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 *  MRU uses LinkedHashMap with access-ordering enabled (accessOrder set to true). This allows the map to maintain
 *  the order based on the access history, with the most recently accessed item at the end. Then, in the
 *  removeEldestEntry method, by overriding the default behavior to remove the eldest (most recently used) entry when
 *  the size exceeds the capacity. This effectively makes it an MRU cache.
 *
 */
public class CacheMRU<K, V> extends CacheBase<K, V> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final LinkedHashMap<K, V> primaryCache;
    private int sizeCapacity;

    public CacheMRU(int sizeCapacity) {
        this.sizeCapacity = sizeCapacity;
        this.primaryCache = new LinkedHashMap<>(sizeCapacity, 0.75f, true);
        this.setPrimaryCache(Collections.synchronizedMap(primaryCache));
    }

    @Override
    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException {
        lock.writeLock().lock();
        try {
            if (this.getPrimaryCache().size() >= this.sizeCapacity) {
                K mostRecentKey = getMostRecentlyUsedKey();
                if (mostRecentKey != null) {
                    this.getPrimaryCache().remove(mostRecentKey);
                    this.cleanupIndexFor(mostRecentKey);
                }
            }
            this.putDefaultImpl(primaryKey, value, secondaryKeys);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void remove(K primaryKey) {
        this.removeDefaultImpl(primaryKey);
    }

    @Override
    public V get(K primaryKey) {
        return this.getDefaultImpl(primaryKey);
    }

    private K getMostRecentlyUsedKey() {
        synchronized (this.getPrimaryCache()) {
            if (this.getPrimaryCache().isEmpty()) return null;

            Iterator<K> it = this.getPrimaryCache().keySet().iterator();
            K current = null;
            while (it.hasNext()) {
                current = it.next(); // Iterate to the end
            }
            return current;
        }
    }

    @Override
    public V getBySecondaryKeyIndexOne(Object secondaryKey) {
        lock.writeLock().lock();
        try {
            if (secondaryKey != null && this.getSecondaryIndexOne().containsKey(secondaryKey)) {
                K key = this.getSecondaryIndexOne().get(secondaryKey);
                V value = this.getPrimaryCache().get(key);
                if (value != null) {
                    this.getPrimaryCache().put(key, value); // maintain MRU status
                }
                return value;
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V getBySecondaryKeyIndexTwo(Object secondaryKey) {
        lock.writeLock().lock();
        try {
            if (secondaryKey != null && this.getSecondaryIndexTwo().containsKey(secondaryKey)) {
                K key = this.getSecondaryIndexTwo().get(secondaryKey);
                V value = this.getPrimaryCache().get(key);
                if (value != null) {
                    this.getPrimaryCache().put(key, value); // Reinsert the accessed entry to maintain its MRU status
                }
                return value;
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }
}

