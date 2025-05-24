package com.vertexcache.core.cache.impl;

import com.vertexcache.core.cache.CacheBase;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheLFU <K, V> extends CacheBase<K, V> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<K, Integer> frequencyMap;
    private int sizeCapacity;

    public CacheLFU(int sizeCapacity) {
        this.sizeCapacity = sizeCapacity;
        this.setPrimaryCache(Collections.synchronizedMap(new LinkedHashMap<>(sizeCapacity, 0.75f, true)));
        this.frequencyMap = Collections.synchronizedMap(new LinkedHashMap<>(sizeCapacity, 0.75f, true));
        this.setSecondaryIndexOne(Collections.synchronizedMap(new LinkedHashMap<>(sizeCapacity, 0.75f, true)));
        this.setSecondaryIndexTwo(Collections.synchronizedMap(new LinkedHashMap<>(sizeCapacity, 0.75f, true)));
    }

    @Override
    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException {
        lock.writeLock().lock();
        try {
            if (secondaryKeys.length > MAX_SECONDARY_INDEXES) {
                throw new VertexCacheTypeException("Too many secondary indexes, maximum 2 allowed.");
            }
            this.getPrimaryCache().put(primaryKey, value);
            frequencyMap.put(primaryKey, frequencyMap.getOrDefault(primaryKey, 0) + 1);
            this.updateSecondaryKeys(primaryKey, secondaryKeys);
            evictIfNecessary();
        } catch (OutOfMemoryError e) {
            throw new VertexCacheTypeException("Out of memory, increase memory or use eviction policy other than none.");
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void remove(K primaryKey) {
        this.removeDefaultImpl(primaryKey);
    }

    @Override
    public V get(K key) {
        lock.writeLock().lock(); // because it mutates frequencyMap
        try {
            V value = this.getPrimaryCache().get(key);
            if (value != null) {
                frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
            }
            return value;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void evictIfNecessary() {
        synchronized (this.getPrimaryCache()) {
            if (this.getPrimaryCache().size() > sizeCapacity) {
                K leastFrequentKey = null;
                int minFrequency = Integer.MAX_VALUE;

                for (Map.Entry<K, Integer> entry : frequencyMap.entrySet()) {
                    if (entry.getValue() < minFrequency) {
                        minFrequency = entry.getValue();
                        leastFrequentKey = entry.getKey();
                    }
                }

                if (leastFrequentKey != null) {
                    this.getPrimaryCache().remove(leastFrequentKey);
                    this.cleanupIndexFor(leastFrequentKey);
                    frequencyMap.remove(leastFrequentKey);
                }
            }
        }
    }
}
