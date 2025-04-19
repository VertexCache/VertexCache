package com.vertexcache.core.cache.impl;

import com.vertexcache.core.cache.CacheBase;
import com.vertexcache.core.cache.VertexCacheTypeException;

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
