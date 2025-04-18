package com.vertexcache.module.ratelimiter;

import com.vertexcache.module.ratelimiter.impl.TokenBucketRateLimiter;

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
