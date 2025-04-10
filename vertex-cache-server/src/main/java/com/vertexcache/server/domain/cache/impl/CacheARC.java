package com.vertexcache.server.domain.cache.impl;

import com.vertexcache.server.exception.VertexCacheTypeException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * ARC (Adaptive Replacement Cache) is a cache replacement algorithm designed to adapt dynamically to changing access
 * patterns to improve cache hit rates and overall performance. It combines the benefits of both LRU
 * (Least Recently Used) and LFU (Least Frequently Used) cache eviction policies.
 *
 * Cache Structure:
 *  - primaryCache: Represents the main cache where frequently accessed items are stored.
 *  - ghostCache: Acts as a secondary cache for items that were recently accessed but not frequently accessed.
 *  - mainEvict: Contains items that were evicted from the main cache.
 *  - ghostEvict: Holds items that were evicted from the ghost cache.
 *
 * Adaptation:
 *   ARC dynamically adjusts the sizes of mainCache and ghostCache based on recent access patterns to optimize cache
 *   performance. It aims to strike a balance between storing frequently accessed items in primaryCache and retaining
 *   recently accessed items in ghostCache.
 *
 * New Item Insertion (PUT):
 *    When a new item is inserted into the cache, ARC first checks if it already exists in either primaryCache or
 *    ghostCache. If found, no further action is taken. If the cache is full (mainCache + ghostCache size exceeds the
 *    capacity), ARC initiates a replacement process to evict an item from either mainCache or ghostCache based on
 *    predefined rules.
 *
 * Cache Hit (GET):
 *    When an item is accessed from the cache (GET operation), ARC first checks if the item exists in either
 *    primaryCache or ghostCache. If found in mainCache, the item is promoted to ghostCache to indicate recent use.
 *    If found in ghostCache, its access frequency is increased, but it remains in ghostCache to maintain its position
 *    as a recently used item.
 *
 * Replacement (REPLACE):
 *   When the cache is full and a replacement is required, ARC selects a victim item from either mainEvict or
 *   ghostEvict based on predefined rules. The selection criteria prioritize evicting items that are less likely to be
 *   accessed again soon, based on both recency and frequency of access.
 *
 */
public class CacheARC<K, V> extends CacheBase<K, V> {

    private Map<K, V> ghostCache = Collections.synchronizedMap(new LinkedHashMap<K, V>());

    private Map<K, V> mainEvict = Collections.synchronizedMap(new LinkedHashMap<K, V>());
    private Map<K, V> ghostEvict = Collections.synchronizedMap(new LinkedHashMap<K, V>());

    private int sizeCapacity;

    public CacheARC(int sizeCapacity) {
        this.sizeCapacity = sizeCapacity;
        this.setPrimaryCache(Collections.synchronizedMap(new LinkedHashMap<K, V>()));
        this.setSecondaryIndexOne(Collections.synchronizedMap(new LinkedHashMap<Object, K>()));
        this.setSecondaryIndexTwo(Collections.synchronizedMap(new LinkedHashMap<Object, K>()));
    }

    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException {
        synchronized (this) {
            if (this.getPrimaryCache().containsKey(primaryKey) || ghostCache.containsKey(primaryKey)) {
                return; // Key already in cache, no need to add
            }

            if (this.getPrimaryCache().size() + ghostCache.size() >= sizeCapacity) {
                if (this.getPrimaryCache().size() < sizeCapacity) {
                    replace();
                } else {
                    if (!this.getPrimaryCache().isEmpty()) {
                        ghostEvict.put(this.getPrimaryCache().keySet().iterator().next(), this.getPrimaryCache().remove(this.getPrimaryCache().keySet().iterator().next()));
                    } else {
                        ghostEvict.put(ghostCache.keySet().iterator().next(), ghostCache.remove(ghostCache.keySet().iterator().next()));
                    }
                }
            }

            this.getPrimaryCache().put(primaryKey, value);
            if (secondaryKeys.length > 0 && secondaryKeys[0] != null) {
                this.getSecondaryIndexOne().put(secondaryKeys[0], primaryKey);
            }
            if (secondaryKeys.length > 1 && secondaryKeys[1] != null) {
                this.getSecondaryIndexTwo().put(secondaryKeys[1], primaryKey);
            }
        }
    }

    @Override
    public V get(K key) {
        synchronized (this) {
            if (this.getPrimaryCache().containsKey(key)) {
                V value = this.getPrimaryCache().remove(key);
                ghostCache.put(key, value);
                return value;
            } else if (ghostCache.containsKey(key)) {
                V value = ghostCache.remove(key);
                ghostCache.put(key, value);
                return value;
            } else {
                return null; // Key not found in cache
            }
        }
    }

    @Override
    public void remove(K primaryKey) {
        synchronized (this) {
            this.getPrimaryCache().remove(primaryKey);
            ghostCache.remove(primaryKey);
            mainEvict.remove(primaryKey);
            ghostEvict.remove(primaryKey);

            // Remove from secondary indexes
            for (Map<Object, K> index : new Map[]{this.getSecondaryIndexOne(), this.getSecondaryIndexTwo()}) {
                index.entrySet().removeIf(entry -> entry.getValue().equals(primaryKey));
            }
        }
    }

    private void replace() {
        synchronized (this) {
            if (!mainEvict.isEmpty() && (this.getPrimaryCache().size() > 0 || this.getPrimaryCache().size() == 0 && !ghostCache.containsKey(mainEvict.keySet().iterator().next()))) {
                K key = mainEvict.keySet().iterator().next();
                mainEvict.remove(key);
                this.getPrimaryCache().put(key, null); // Add key to mainCache
            } else {
                K key = ghostEvict.keySet().iterator().next();
                ghostEvict.remove(key);
                ghostCache.put(key, null); // Add key to ghostCache
            }
        }
    }
}
