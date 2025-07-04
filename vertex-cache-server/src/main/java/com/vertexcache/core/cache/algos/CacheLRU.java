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
import org.apache.commons.collections4.map.LRUMap;

import java.util.Collections;

/*
 * By using the LRUMap, LRU is automatically handled
 *
 *  LRU (Least Recently Used) removal is handled for both the primary cache and the corresponding secondary indexes.
 *  The LRUMap from Apache Commons Collections is used for both the primary cache and the secondary indexes, ensuring
 *  that least recently used entries are evicted when the cache reaches its maximum capacity.
 *
 *  When an entry is accessed or added to the primary cache, it becomes the most recently used entry. If the cache is
 *  full, the least recently used entry in the primary cache is automatically evicted. Similarly, when an entry is
 *  accessed or added to a secondary index, it becomes the most recently used entry in that index. If the secondary
 *  index is full, the least recently used entry in that index is automatically evicted.
 *
 */
public class CacheLRU<K, V> extends CacheBase<K, V> {

    public CacheLRU(int sizeCapacity) {
        this.setPrimaryCache(Collections.synchronizedMap(new LRUMap<>(sizeCapacity)));
        this.setSecondaryIndexOne(Collections.synchronizedMap(new LRUMap<>(sizeCapacity)));
        this.setSecondaryIndexTwo(Collections.synchronizedMap(new LRUMap<>(sizeCapacity)));
    }

    @Override
    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException {
        this.putDefaultImpl(primaryKey,value,secondaryKeys);
    }

    @Override
    public V get(K primaryKey) {
        return this.getDefaultImpl(primaryKey);
    }

    @Override
    public void remove(K primaryKey) {
        this.removeDefaultImpl(primaryKey);
    }


}
