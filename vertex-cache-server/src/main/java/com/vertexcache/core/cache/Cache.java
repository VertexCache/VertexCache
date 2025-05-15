package com.vertexcache.core.cache;

import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import com.vertexcache.core.cache.impl.*;

import java.util.Set;

public class Cache<K, V> {

    private static volatile Cache<?, ?> instance;
    private final CacheBase<K, CacheEntry<V>> cache;

    private Cache(EvictionPolicy evictionPolicy, int sizeCapacity) {
        switch (evictionPolicy) {
            case LRU: cache = new CacheLRU<>(sizeCapacity); break;
            case MRU: cache = new CacheMRU<>(sizeCapacity); break;
            case FIFO: cache = new CacheFIFO<>(sizeCapacity); break;
            case LFU: cache = new CacheLFU<>(sizeCapacity); break;
            case RANDOM: cache = new CacheRandom<>(sizeCapacity); break;
            case ARC: cache = new CacheARC<>(sizeCapacity); break;
            case TwoQueues: cache = new CacheTwoQueues<>(sizeCapacity); break;
            case Clock: cache = new CacheClock<>(sizeCapacity); break;
            case TinyLFU: cache = new CacheTinyLFU<>(sizeCapacity); break;
            case NONE:
            default: cache = new CacheNoEviction<>(); break;
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
        return getInstance(evictionPolicy, 0);
    }

    public static <K, V> Cache<K, V> getInstance() throws Exception {
        if (instance == null) {
            throw new Exception("Cache not yet initialized with eviction policy");
        }
        return (Cache<K, V>) instance;
    }

    public void put(K primaryKey, V value, String... secondaryKeys) throws VertexCacheTypeException {
        cache.put(primaryKey, new CacheEntry<>(value, false), secondaryKeys);
    }

    public void upsert(K key, V value, String... secondaryKeys) throws VertexCacheTypeException {
        CacheEntry<V> existing = cache.get(key);
        if (existing != null) {
            existing.updateValue(value);
        } else {
            put(key, value, secondaryKeys);
        }
    }

    public V get(K primaryKey) {
        CacheEntry<V> entry = cache.get(primaryKey);
        return entry != null ? entry.getValue() : null;
    }

    public V getBySecondaryKeyIndexOne(Object secondaryKey) {
        CacheEntry<V> entry = cache.getBySecondaryKeyIndexOne(secondaryKey);
        return entry != null ? entry.getValue() : null;
    }

    public V getBySecondaryKeyIndexTwo(Object secondaryKey) {
        CacheEntry<V> entry = cache.getBySecondaryKeyIndexTwo(secondaryKey);
        return entry != null ? entry.getValue() : null;
    }

    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    public boolean containsValue(V value) {
        return cache.keySet().stream()
                .map(k -> cache.get(k))
                .anyMatch(e -> e != null && e.getValue().equals(value));
    }

    public void remove(K primaryKey) {
        cache.remove(primaryKey);
    }

    public int size() {
        return cache.size();
    }

    public void clear() {
        cache.clear();
    }

    public Set<String> keySet() {
        return (Set<String>) cache.keySet();
    }

    // Optional inspection utility
    public String inspect(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) return "Key not found";
        return String.format(
                "createdAt=%d, lastAccessed=%d, lastUpdatedAt=%d, hitCount=%d, remote=%s",
                entry.getCreatedAt(),
                entry.getLastAccessed(),
                entry.getLastUpdatedAt(),
                entry.getHitCount(),
                entry.isRemote()
        );
    }
}
