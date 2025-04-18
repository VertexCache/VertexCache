package com.vertexcache.module.ratelimiter.impl;

import java.util.concurrent.atomic.AtomicInteger;

public class TokenBucketRateLimiter {
    private static final long ONE_SECOND_NANOS = 1_000_000_000L;
    private final int maxTokens;
    private final int refillRatePerSecond;
    private final AtomicInteger tokens;
    private long lastRefillTimestamp;

    public TokenBucketRateLimiter(int maxTokens, int refillRatePerSecond) {
        this.maxTokens = maxTokens;
        this.refillRatePerSecond = refillRatePerSecond;
        this.tokens = new AtomicInteger(maxTokens);
        this.lastRefillTimestamp = System.nanoTime();
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens.get() > 0) {
            tokens.decrementAndGet();
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsedSeconds = (now - lastRefillTimestamp) / ONE_SECOND_NANOS;
        if (elapsedSeconds > 0) {
            int refill = (int) (elapsedSeconds * refillRatePerSecond);
            int newTokens = Math.min(maxTokens, tokens.get() + refill);
            tokens.set(newTokens);
            lastRefillTimestamp = now;
        }
    }
}
