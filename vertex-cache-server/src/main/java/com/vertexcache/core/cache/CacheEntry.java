package com.vertexcache.core.cache;

import java.util.concurrent.atomic.AtomicInteger;

public final class CacheEntry<V> {
    private V value;
    private final long createdAt;
    private volatile long lastAccessed;
    private volatile long lastUpdatedAt;
    private final AtomicInteger hitCount;
    private final boolean isRemote;

    public CacheEntry(V value, boolean isRemote) {
        long now = System.currentTimeMillis();
        this.value = value;
        this.createdAt = now;
        this.lastAccessed = now;
        this.lastUpdatedAt = now;
        this.hitCount = new AtomicInteger(0);
        this.isRemote = isRemote;
    }

    public synchronized void updateValue(V newValue) {
        this.value = newValue;
        this.lastUpdatedAt = System.currentTimeMillis();
    }

    public V getValue() {
        this.lastAccessed = System.currentTimeMillis();
        this.hitCount.incrementAndGet();
        return value;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastAccessed() {
        return lastAccessed;
    }

    public long getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public int getHitCount() {
        return hitCount.get();
    }

    public boolean isRemote() {
        return isRemote;
    }
}
