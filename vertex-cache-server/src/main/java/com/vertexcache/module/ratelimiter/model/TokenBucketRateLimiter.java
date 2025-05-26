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
package com.vertexcache.module.ratelimiter.model;

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
