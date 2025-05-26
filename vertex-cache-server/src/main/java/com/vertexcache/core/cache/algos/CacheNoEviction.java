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

/*
 * A simple cache strategy that performs no automatic eviction.
 *
 * This implementation allows the cache to grow without bounds,
 * limited only by the JVM’s memory. It is best suited for use cases
 * where the dataset is known to be small or externally managed,
 * such as static configuration or always-hot reference data.
 *
 * Key characteristics:
 * - No size enforcement (sizeCapacity is ignored)
 * - No eviction is ever triggered automatically
 * - All entries remain until explicitly removed
 *
 * Secondary indexes (idx1, idx2) are supported, and reverse indexing
 * is used for efficient cleanup of associated mappings.
 *
 * Use with caution in environments with limited memory.
 */
public class CacheNoEviction<K, V> extends CacheBase<K, V> {


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
