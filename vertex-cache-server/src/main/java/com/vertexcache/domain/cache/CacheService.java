package com.vertexcache.domain.cache;

import com.vertexcache.domain.cache.impl.Cache;
import com.vertexcache.domain.cache.impl.EvictionPolicy;

public class CacheService {
    private static volatile CacheService instance;
    private final Cache<?, ?> cache;

    private CacheService() {
        // Initialize the cache with default eviction policy
        cache = new Cache<>(EvictionPolicy.NONE);
    }

    private CacheService(EvictionPolicy evictionPolicy, int sizeCapacity) {
        cache = new Cache<>(evictionPolicy, sizeCapacity);
    }

    public static CacheService getInstance() {
        if (instance == null) {
            synchronized (CacheService.class) {
                if (instance == null) {
                    instance = new CacheService();
                }
            }
        }
        return instance;
    }

    public static CacheService getInstance(EvictionPolicy evictionPolicy, int sizeCapacity) {
        if (instance == null) {
            synchronized (CacheService.class) {
                if (instance == null) {
                    instance = new CacheService(evictionPolicy, sizeCapacity);
                }
            }
        }
        return instance;
    }

    public Cache<?, ?> getCache() {
        return cache;
    }
}
