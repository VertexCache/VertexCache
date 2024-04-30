package com.vertexcache.server.domain.cache.impl;

import com.vertexcache.server.exception.VertexCacheException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * TinyLFU strikes a balance between access frequency and recency while optimizing cache utilization and hit ratios.
 * By incorporating probabilistic admission and a hybrid eviction policy, it effectively manages cache entries to
 * improve overall system performance and efficiency, particularly in scenarios with skewed access patterns and
 * dynamic workloads.
 * 
 */
public class CacheTinyLFU<K, V> extends CacheBase<K, V> {

    private int sizeCapacity;
    private final Map<K, Integer> frequencyMap;
    private final ReentrantReadWriteLock lock;

    public CacheTinyLFU(int sizeCapacity) {
        this.sizeCapacity = sizeCapacity;
        this.setPrimaryCache(new ConcurrentHashMap<>());
        this.frequencyMap = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.setSecondaryIndexOne(new ConcurrentHashMap<>());
        this.setSecondaryIndexTwo(new ConcurrentHashMap<>());
    }

    @Override
    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheException {
        lock.writeLock().lock();
        try {
            if (this.getPrimaryCache().size() >= sizeCapacity) {
                evict();
            }
            this.getPrimaryCache().put(primaryKey, value);
            frequencyMap.put(primaryKey, 1); // Initially set frequency to 1
            this.updateSecondaryKeys(primaryKey,secondaryKeys);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V get(K key) {
        lock.readLock().lock();
        try {
            V value = this.getPrimaryCache().get(key);
            if (value != null) {
                incrementFrequency(key);
            }
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void remove(K key) {
        lock.writeLock().lock();
        try {
            this.getPrimaryCache().remove(key);
            frequencyMap.remove(key);
            // Remove mappings from secondary keys to primary key
            for (Map.Entry<Object, K> entry : this.getSecondaryIndexOne().entrySet()) {
                if (entry.getValue().equals(key)) {
                    this.getSecondaryIndexOne().remove(entry.getKey());
                }
            }
            for (Map.Entry<Object, K> entry : this.getSecondaryIndexTwo().entrySet()) {
                if (entry.getValue().equals(key)) {
                    this.getSecondaryIndexTwo().remove(entry.getKey());
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void evict() {
        int minFreq = Integer.MAX_VALUE;
        K keyToRemove = null;
        for (Map.Entry<K, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() < minFreq) {
                minFreq = entry.getValue();
                keyToRemove = entry.getKey();
            }
        }
        if (keyToRemove != null) {
            this.getPrimaryCache().remove(keyToRemove);
            frequencyMap.remove(keyToRemove);
            // Remove mappings from secondary keys to evicted primary key
            for (Map.Entry<Object, K> entry : this.getSecondaryIndexOne().entrySet()) {
                if (entry.getValue().equals(keyToRemove)) {
                    this.getSecondaryIndexOne().remove(entry.getKey());
                }
            }
            for (Map.Entry<Object, K> entry : this.getSecondaryIndexTwo().entrySet()) {
                if (entry.getValue().equals(keyToRemove)) {
                    this.getSecondaryIndexTwo().remove(entry.getKey());
                }
            }
        }
    }

    private void incrementFrequency(K key) {
        lock.writeLock().lock();
        try {
            int currentFrequency = frequencyMap.getOrDefault(key, 0);
            frequencyMap.put(key, currentFrequency + 1);
        } finally {
            lock.writeLock().unlock();
        }
    }

}
