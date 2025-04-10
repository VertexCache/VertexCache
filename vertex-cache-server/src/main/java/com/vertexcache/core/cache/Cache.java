package com.vertexcache.core.cache;

import com.vertexcache.core.cache.impl.*;
import com.vertexcache.core.exception.VertexCacheTypeException;

public class Cache<K, V> {

    private static volatile Cache<?, ?> instance;
    private final CacheBase<K, V> cache;

    private Cache(EvictionPolicy evictionPolicy, int sizeCapacity) {
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
            case ARC:
                cache = new CacheARC<>(sizeCapacity);
                break;
            case TwoQueues:
                cache = new CacheTwoQueues<>(sizeCapacity);
                break;
            case Clock:
                cache = new CacheClock<>(sizeCapacity);
                break;
            case TinyLFU:
                cache = new CacheTinyLFU<>(sizeCapacity);
                break;
            case NONE:
            default:
                cache = new CacheNoEviction<>();
                break;
        }
    }

    public static <K, V> Cache<K, V> getInstance(EvictionPolicy evictionPolicy, int sizeCapacity) {
        if (instance == null) {
            synchronized (Cache.class) {
                if (instance == null) {
                    instance = new Cache<>(evictionPolicy, sizeCapacity);
                }
            }
        }
        return (Cache<K, V>) instance;
    }

    public static <K, V> Cache<K, V> getInstance(EvictionPolicy evictionPolicy) {
        if (instance == null) {
            synchronized (Cache.class) {
                if (instance == null) {
                    instance = new Cache<>(evictionPolicy, 0);
                }
            }
        }
        return (Cache<K, V>) instance;
    }

    public static <K, V> Cache<K, V> getInstance() throws Exception {
        if (instance == null) {
            synchronized (Cache.class) {
                if (instance == null) {
                    throw new Exception("Cache not yet initialized with eviction policy");
                }
            }
        }
        return (Cache<K, V>) instance;
    }

    public void put(K primaryKey, V value, String... secondaryKeys) throws VertexCacheTypeException {
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

    public void remove(K primaryKey) { cache.remove(primaryKey);}

    public int size() {
        return cache.size();
    }

    public void clear() {
        cache.clear();
    }
}

