package com.vertexcache.module.ratelimiter.service;

import com.vertexcache.module.ratelimiter.model.TokenBucketRateLimiter;

public class RateLimiterManager {
    private static final RateLimiterManager instance = new RateLimiterManager();
    private TokenBucketRateLimiter limiter;

    private RateLimiterManager() {}

    public static RateLimiterManager getInstance() {
        return instance;
    }

    public void init(TokenBucketRateLimiter limiter) {
        this.limiter = limiter;
    }

    public boolean allowCommand() {
        return limiter != null && limiter.allowRequest();
    }
}
