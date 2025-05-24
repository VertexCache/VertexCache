package com.vertexcache.core.cache;

import com.vertexcache.core.cache.exception.VertexCacheTypeException;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

abstract public class CacheBase<K, V> {

    protected static final int MAX_SECONDARY_INDEXES = 2;

    private Map<K, V> primaryCache = new ConcurrentHashMap<>();
    private Map<Object, K> secondaryIndexOne = new ConcurrentHashMap<>();
    private Map<Object, K> secondaryIndexTwo = new ConcurrentHashMap<>();
    private final Map<K, CacheIndexRef> reverseIndex = new ConcurrentHashMap<>();

    abstract public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException;
    abstract public V get(K primaryKey);
    abstract public void remove(K primaryKey);

    protected void putDefaultImpl(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException {
        if (secondaryKeys.length <= MAX_SECONDARY_INDEXES) {
            try {
                synchronized (this.getPrimaryCache()) {
                    this.getPrimaryCache().put(primaryKey, value);
                }
                updateSecondaryKeys(primaryKey, secondaryKeys);
            } catch (OutOfMemoryError e) {
                throw new VertexCacheTypeException("Out of memory, increase memory or use eviction policy other than none.");
            }
        } else {
            throw new VertexCacheTypeException("Too many secondary index, maximum 2 allowed.");
        }
    }

    protected void updateSecondaryKeys(K primaryKey, Object... secondaryKeys) {
        Object idx1 = null;
        Object idx2 = null;

        if (secondaryKeys.length > 0 && secondaryKeys[0] != null) {
            idx1 = secondaryKeys[0];
            secondaryIndexOne.put(idx1, primaryKey);
        }

        if (secondaryKeys.length > 1 && secondaryKeys[1] != null) {
            idx2 = secondaryKeys[1];
            secondaryIndexTwo.put(idx2, primaryKey);
        }

        if (idx1 != null || idx2 != null) {
            reverseIndex.put(primaryKey, new CacheIndexRef(idx1, idx2));
        } else {
            // Clear any existing stale mapping
            reverseIndex.remove(primaryKey);
        }
    }

    public void removeDefaultImpl(K primaryKey) {
        synchronized (this) {
            primaryCache.remove(primaryKey);
            cleanupIndexFor(primaryKey);
        }
    }

    protected void cleanupIndexFor(K key) {
        CacheIndexRef ref = reverseIndex.remove(key);
        if (ref != null) {
            if (ref.idx1 != null) secondaryIndexOne.remove(ref.idx1);
            if (ref.idx2 != null) secondaryIndexTwo.remove(ref.idx2);
        }
    }

    public V getDefaultImpl(K primaryKey) {
        synchronized (this) {
            return this.getPrimaryCache().get(primaryKey);
        }
    }

    public V getBySecondaryKeyIndexOne(Object secondaryKey) {
        synchronized (this) {
            if (secondaryKey != null && getSecondaryIndexOne().containsKey(secondaryKey)) {
                K primaryCacheKey = getSecondaryIndexOne().get(secondaryKey);
                if (primaryCacheKey != null) {
                    return getPrimaryCache().get(primaryCacheKey);
                }
            }
        }
        return null;
    }

    public V getBySecondaryKeyIndexTwo(Object secondaryKey) {
        synchronized (this) {
            if (secondaryKey != null && getSecondaryIndexTwo().containsKey(secondaryKey)) {
                K primaryCacheKey = getSecondaryIndexTwo().get(secondaryKey);
                if (primaryCacheKey != null) {
                    return getPrimaryCache().get(primaryCacheKey);
                }
            }
        }
        return null;
    }

    public synchronized boolean containsKey(K key) {
        return this.getPrimaryCache().containsKey(key);
    }

    public synchronized boolean containsValue(V value) {
        return this.getPrimaryCache().containsValue(value);
    }

    public synchronized int size() {
        return this.getPrimaryCache().size();
    }

    public void clear() {
        synchronized (this.getPrimaryCache()) {
            this.getPrimaryCache().clear();
        }
        synchronized (secondaryIndexOne) {
            this.getSecondaryIndexOne().clear();
        }
        synchronized (secondaryIndexTwo) {
            this.getSecondaryIndexTwo().clear();
        }
    }

    protected Map<K, V> getPrimaryCache() {
        return primaryCache;
    }

    protected void setPrimaryCache(Map<K, V> primaryCache) {
        this.primaryCache = primaryCache;
    }

    protected Map<Object, K> getSecondaryIndexOne() {
        return secondaryIndexOne;
    }

    protected void setSecondaryIndexOne(Map<Object, K> secondaryIndexOne) {
        this.secondaryIndexOne = secondaryIndexOne;
    }

    protected Map<Object, K> getSecondaryIndexTwo() {
        return secondaryIndexTwo;
    }

    protected void setSecondaryIndexTwo(Map<Object, K> secondaryIndexTwo) {
        this.secondaryIndexTwo = secondaryIndexTwo;
    }

    public synchronized Set<K> keySet() {
        return this.getPrimaryCache().keySet();
    }

    public Map<Object, K> getReadOnlySecondaryIndexOne() {
        return Collections.unmodifiableMap(secondaryIndexOne);
    }

    public Map<Object, K> getReadOnlySecondaryIndexTwo() {
        return Collections.unmodifiableMap(secondaryIndexTwo);
    }
}
