package com.vertexcache.core.cache.impl;

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
