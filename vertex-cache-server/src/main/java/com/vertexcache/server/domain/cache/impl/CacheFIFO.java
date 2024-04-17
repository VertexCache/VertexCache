package com.vertexcache.server.domain.cache.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * The LinkedHashMaps for the primary cache and secondary indexes are initialized with false as the last parameter in
 * their constructors, which disables the access order feature and thus implements FIFO eviction.
 *
 * The secondary indexes (secondaryIndexOne and secondaryIndexTwo) are not configured to have a maximum capacity or
 * an eviction policy like the primary cache. Therefore, they do not need to use the removeEldestEntry method.
 *
 * The purpose of using removeEldestEntry in the primary cache (cache) is to ensure that when the cache size exceeds
 * the specified capacity, the oldest entry (according to insertion order) is automatically removed to make space for
 * new entries. This is a feature provided by LinkedHashMap when constructed with the accessOrder parameter set to
 * true, as it orders the entries based on their access order.
 *
 * However, the secondary indexes are not ordered based on access order, and they are not expected to have their
 * entries evicted automatically based on any policy. They are simply used to provide quick access to primary cache
 * keys based on secondary keys. Therefore, there's no need to define an eviction policy or utilize removeEldestEntry
 * for the secondary indexes.
 *
 */
public class CacheFIFO<K, V> extends CacheBase<K, V> {

    private static final float LOAD_FACTOR = 0.75f;

    public CacheFIFO(int sizeCapacity) {
        // Load Factor is set at 75%, in otherwords rehashed when they are 75% full,
        this.setPrimaryCache(Collections.synchronizedMap(new LinkedHashMap<>(sizeCapacity, LOAD_FACTOR, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > sizeCapacity;
            }
        }));
        this.setSecondaryIndexOne(Collections.synchronizedMap(new LinkedHashMap<>(sizeCapacity, LOAD_FACTOR, false)));
        this.setSecondaryIndexTwo(Collections.synchronizedMap(new LinkedHashMap<>(sizeCapacity, LOAD_FACTOR, false)));
    }
}
