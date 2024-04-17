package com.vertexcache.server.domain.cache.impl;

import org.apache.commons.collections4.map.LRUMap;

import java.util.Collections;

/*
 * By using the LRUMap, LRU is automatically handled
 *
 *  LRU (Least Recently Used) removal is handled for both the primary cache and the corresponding secondary indexes.
 *  The LRUMap from Apache Commons Collections is used for both the primary cache and the secondary indexes, ensuring
 *  that least recently used entries are evicted when the cache reaches its maximum capacity.
 *
 *  When an entry is accessed or added to the primary cache, it becomes the most recently used entry. If the cache is
 *  full, the least recently used entry in the primary cache is automatically evicted. Similarly, when an entry is
 *  accessed or added to a secondary index, it becomes the most recently used entry in that index. If the secondary
 *  index is full, the least recently used entry in that index is automatically evicted.
 *
 */
public class CacheLRU<K, V> extends CacheBase<K, V> {

    public CacheLRU(int sizeCapacity) {
        this.setPrimaryCache(Collections.synchronizedMap(new LRUMap<>(sizeCapacity)));
        this.setSecondaryIndexOne(Collections.synchronizedMap(new LRUMap<>(sizeCapacity)));
        this.setSecondaryIndexTwo(Collections.synchronizedMap(new LRUMap<>(sizeCapacity)));
    }

}
