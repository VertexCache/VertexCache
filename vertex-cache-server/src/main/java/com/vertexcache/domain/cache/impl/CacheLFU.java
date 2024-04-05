package com.vertexcache.domain.cache.impl;

import com.vertexcache.exception.VertexCacheException;

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

    public void put(K key, V value, Object... secondaryKeys) {
        synchronized (this.getPrimaryCache()) {
            this.getPrimaryCache().put(key, value);
        }
        frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);

        for (int i = 0; i < Math.min(secondaryKeys.length, 2); i++) {
            switch (i) {
                case 0:
                    synchronized (this.getSecondaryIndexOne()) {
                        this.getSecondaryIndexOne().put(secondaryKeys[i], key);
                    }
                    break;
                case 1:
                    synchronized (this.getSecondaryIndexTwo()) {
                        this.getSecondaryIndexTwo().put(secondaryKeys[i], key);
                    }
                    break;
            }
        }

        evictIfNecessary();
    }

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
