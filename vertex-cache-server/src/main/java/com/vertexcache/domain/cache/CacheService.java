package com.vertexcache.domain.cache;

import com.vertexcache.domain.cache.impl.Cache;
import com.vertexcache.domain.cache.impl.EvictionPolicy;
import com.vertexcache.exception.VertexCacheException;

public class CacheService {
    private static volatile CacheService instance;
    private static Cache<?, ?> cache;

    private CacheService() {
        // Initialize the cache with default eviction policy
        cache = new Cache<>(EvictionPolicy.NONE);
    }

    private CacheService(EvictionPolicy evictionPolicy, int sizeCapacity) {
        cache = new Cache<>(evictionPolicy, sizeCapacity);
    }

    public static Cache<?, ?> getInstance() throws Exception {
        if (instance == null) {
            synchronized (CacheService.class) {
                if (instance == null) {
                    instance = new CacheService();
                }
            }
        }
        return instance.getCache();
    }

    public static Cache<?, ?> getInstance(EvictionPolicy evictionPolicy, int sizeCapacity) throws Exception {
        if (instance == null) {
            synchronized (CacheService.class) {
                if (instance == null) {
                    instance = new CacheService(evictionPolicy, sizeCapacity);
                }
            }
        }
        return instance.getCache();
    }

    public static Cache<?, ?> getCache() throws Exception {
        if(cache != null) {
            return cache;
        } else {

                throw new VertexCacheException("CacheService instance not initialized");

        }
    }
}