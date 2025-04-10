package com.vertexcache.core.cache.impl;

import com.vertexcache.core.exception.VertexCacheTypeException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CacheLFU <K, V> extends CacheBase<K, V> {

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
        if(secondaryKeys.length <= MAX_SECONDARY_INDEXES) {
            try {

                synchronized (this.getPrimaryCache()) {
                    this.getPrimaryCache().put(primaryKey, value);
                }
                frequencyMap.put(primaryKey, frequencyMap.getOrDefault(primaryKey, 0) + 1);

                synchronized (this.getSecondaryIndexOne()) {
                    if (secondaryKeys.length > 0 && secondaryKeys[0] != null) {
                        this.getSecondaryIndexOne().put(secondaryKeys[0], primaryKey);
                    }
                }
                synchronized (this.getSecondaryIndexTwo()) {
                    if (secondaryKeys.length > 1 && secondaryKeys[1] != null) {
                        this.getSecondaryIndexTwo().put(secondaryKeys[1], primaryKey);
                    }
                }
            } catch (OutOfMemoryError e) {
                // This still potentially can occur even with LRU
                throw new VertexCacheTypeException("Out of memory, increase memory or use eviction policy other than none.");
            }

            evictIfNecessary();

        } else {
            throw new VertexCacheTypeException("Too many secondary index, maximum 2 allowed.");
        }
    }

    @Override
    public void remove(K primaryKey) {
        this.removeDefaultImpl(primaryKey);
    }

    @Override
    public V get(K key) {
        V value;
        synchronized (this.getPrimaryCache()) {
            value = this.getPrimaryCache().get(key);
        }
        if (value != null) {
            frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
        }
        return value;
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
                    frequencyMap.remove(leastFrequentKey);
                    removeKeyFromSecondaryIndexes(leastFrequentKey);
                }
            }
        }
    }

    private void removeKeyFromSecondaryIndexes(K key) {
        synchronized (this.getSecondaryIndexOne()) {
            this.getSecondaryIndexOne().entrySet().removeIf(entry -> Objects.equals(entry.getValue(), key));
        }
        synchronized (this.getSecondaryIndexTwo()) {
            this.getSecondaryIndexTwo().entrySet().removeIf(entry -> Objects.equals(entry.getValue(), key));
        }
    }

}
