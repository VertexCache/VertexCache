package com.vertexcache.domain.cache.impl;

import com.vertexcache.exception.VertexCacheException;

public class Cache<K, V> {

    private final CacheBase<K, V> cache;

    public Cache(EvictionPolicy evictionPolicy, int sizeCapacity) {
        switch (evictionPolicy) {
            case LRU:
                cache = new CacheLRU<>(sizeCapacity);
                break;
            case MRU:
                cache = new CacheMRU<>(sizeCapacity);
                break;
            case FIFO:
                cache = new CacheFIFO<>(sizeCapacity);
                break;
            case LFU:
                cache = new CacheLFU<>(sizeCapacity);
                break;
            case RANDOM:
                cache = new CacheRandom<>(sizeCapacity);
                break;
            case NONE:
            default:
                cache = new CacheNoEviction<>();
                break;
        }
    }

    public Cache(EvictionPolicy evictionPolicy) {
        switch (evictionPolicy) {
            case LRU:
                throw new IllegalArgumentException("LRU eviction policy requires a capacity parameter");
            case NONE:
            default:
                cache = new CacheNoEviction<>();
                break;
        }
    }

    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheException {
        cache.put(primaryKey, value, secondaryKeys);
    }

    public V get(K primaryKey) {
        return cache.get(primaryKey);
    }

    public V getBySecondaryKeyIndexOne(Object secondaryKey) {
        return cache.getBySecondaryKeyIndexOne(secondaryKey);
    }

    public V getBySecondaryKeyIndexTwo(Object secondaryKey) {
        return cache.getBySecondaryKeyIndexTwo(secondaryKey);
    }

    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    public boolean containsValue(V value) {
        return cache.containsValue(value);
    }

    public int size() {
        return cache.size();
    }

    public void clear() {
        cache.clear();
    }
}
