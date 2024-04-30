package com.vertexcache.server.domain.cache.impl;

import com.vertexcache.server.exception.VertexCacheException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract public class CacheBase<K, V> {

    protected static final int MAX_SECONDARY_INDEXES = 2;

    private Map<K, V> primaryCache = new ConcurrentHashMap<>();
    private Map<Object, K> secondaryIndexOne = new ConcurrentHashMap<>();
    private Map<Object, K> secondaryIndexTwo = new ConcurrentHashMap<>();

    abstract public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheException;
    abstract public V get(K primaryKey);
    abstract public void remove(K primaryKey);

    protected void putDefaultImpl(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheException {
        if(secondaryKeys.length <= MAX_SECONDARY_INDEXES) {
            try {
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
                // This still potentially can occur even with LRU
                throw new VertexCacheException("Out of memory, increase memory or use eviction policy other than none.");
            }
        } else {
            throw new VertexCacheException("Too many secondary index, maximum 2 allowed.");
        }
    }

    public void removeDefaultImpl(K primaryKey) {
        synchronized (this) {
            primaryCache.remove(primaryKey);
        }
        synchronized (secondaryIndexOne) {
            secondaryIndexOne.values().removeIf(k -> k.equals(primaryKey));
        }
        synchronized (secondaryIndexTwo) {
            secondaryIndexTwo.values().removeIf(k -> k.equals(primaryKey));
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
        if(!this.getPrimaryCache().isEmpty()) {
            return this.getPrimaryCache().size();
        }  else {
            return 0;
        }
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

    protected void updateSecondaryKeys(K primaryKey, Object... secondaryKeys) {
        for (int i = 0; i < secondaryKeys.length; i++) {
            if (secondaryKeys[i] != null) {
                if (i == 0) {
                    this.getSecondaryIndexOne().put((K) secondaryKeys[i], primaryKey);
                } else if (i == 1) {
                    this.getSecondaryIndexTwo().put((K) secondaryKeys[i], primaryKey);
                }
            }
        }
        /*
              if (secondaryKeys.length > 0 && secondaryKeys[0] != null) {
                this.getSecondaryIndexOne().put((K) secondaryKeys[0], primaryKey);
            }
            if (secondaryKeys.length > 1 && secondaryKeys[1] != null) {
                this.getSecondaryIndexTwo().put((K) secondaryKeys[1], primaryKey);
            }
         */
    }
}
