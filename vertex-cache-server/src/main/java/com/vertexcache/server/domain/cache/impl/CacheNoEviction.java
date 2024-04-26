package com.vertexcache.server.domain.cache.impl;

import com.vertexcache.server.exception.VertexCacheException;

public class CacheNoEviction<K, V> extends CacheBase<K, V> {


    @Override
    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheException {
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
