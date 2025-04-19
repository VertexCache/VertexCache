package com.vertexcache.core.cache.impl;

import com.vertexcache.core.cache.CacheBase;
import com.vertexcache.core.cache.VertexCacheTypeException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 *
 *  MRU uses LinkedHashMap with access-ordering enabled (accessOrder set to true). This allows the map to maintain
 *  the order based on the access history, with the most recently accessed item at the end. Then, in the
 *  removeEldestEntry method, by overriding the default behavior to remove the eldest (most recently used) entry when
 *  the size exceeds the capacity. This effectively makes it an MRU cache.
 *
 */
public class CacheMRU<K, V> extends CacheBase<K, V> {

    public CacheMRU(int sizeCapacity) {
        this.setPrimaryCache(Collections.synchronizedMap(new LinkedHashMap<>(sizeCapacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > sizeCapacity;
            }
        }));
        this.setSecondaryIndexOne(createSecondaryIndex(sizeCapacity));
        this.setSecondaryIndexTwo(createSecondaryIndex(sizeCapacity));
    }

    private Map<Object, K> createSecondaryIndex(int sizeCapacity) {
        return Collections.synchronizedMap(new LinkedHashMap<>(sizeCapacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, K> eldest) {
                return size() > sizeCapacity;
            }
        });
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

    @Override
    public V getBySecondaryKeyIndexOne(Object secondaryKey) {
        if (secondaryKey != null && this.getSecondaryIndexOne().containsKey(secondaryKey)) {
            K key = this.getSecondaryIndexOne().get(secondaryKey);
            V value = this.getPrimaryCache().get(key);
            if (value != null) {
                // Reinsert the accessed entry to maintain its MRU status
                this.getPrimaryCache().put(key, value);
            }
            return value;
        }
        return null;
    }

    @Override
    public V getBySecondaryKeyIndexTwo(Object secondaryKey) {
        if (secondaryKey != null && this.getSecondaryIndexTwo().containsKey(secondaryKey)) {
            K key = this.getSecondaryIndexTwo().get(secondaryKey);
            V value = this.getPrimaryCache().get(key);
            if (value != null) {
                this.getPrimaryCache().put(key, value); // Reinsert the accessed entry to maintain its MRU status
            }
            return value;
        }
        return null;
    }
}

