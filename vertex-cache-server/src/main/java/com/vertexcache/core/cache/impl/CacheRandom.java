package com.vertexcache.core.cache.impl;

import com.vertexcache.core.exception.VertexCacheTypeException;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class CacheRandom<K, V> extends CacheBase<K, V> {

    private final int sizeCapacity;
    private final Random random;

    public CacheRandom(int sizeCapacity) {
        this.sizeCapacity = sizeCapacity;
        this.setPrimaryCache(new ConcurrentHashMap<>());
        this.setSecondaryIndexOne(new ConcurrentHashMap<>());
        this.setSecondaryIndexTwo(new ConcurrentHashMap<>());
        this.random = new Random();
    }

    @Override
    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException {
        if(secondaryKeys.length <= MAX_SECONDARY_INDEXES) {
            try {
                if (this.getPrimaryCache().size() >= this.sizeCapacity) {
                    evictRandom();
                }
                synchronized (this.getPrimaryCache()) {
                    this.getPrimaryCache().put(primaryKey, value);
                }
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
                throw new VertexCacheTypeException("Out of memory, increase memory or use eviction policy other than none.");
            }
        } else {
            throw new VertexCacheTypeException("Too many secondary index, maximum 2 allowed.");
        }
    }

    @Override
    public V get(K primaryKey) {
        return this.getDefaultImpl(primaryKey);
    }

    @Override
    public void remove(K primaryKey) {
        this.removeDefaultImpl(primaryKey);
    }

    private void evictRandom() {
        if (this.getPrimaryCache().isEmpty()) {
            return; // If cache is empty, nothing to evict
        }

        int randomIndex = random.nextInt(this.getPrimaryCache().size());
        K[] keys = this.getPrimaryCache().keySet().toArray((K[]) new Object[0]);
        K keyToRemove = keys[randomIndex];
        this.getPrimaryCache().remove(keyToRemove);
        removeKeyFromSecondaryIndexes(keyToRemove);
    }

    private void removeKeyFromSecondaryIndexes(K key) {
        this.getSecondaryIndexOne().values().removeIf(k -> k.equals(key));
        this.getSecondaryIndexTwo().values().removeIf(k -> k.equals(key));
    }

    private Object generateRandomKey() {
        return random.nextInt(); // Or any suitable random key generation mechanism
    }
}
