/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.core.cache.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a single value entry stored in the cache, associated with a specific key.
 * This class encapsulates the actual value and metadata.
 */
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
