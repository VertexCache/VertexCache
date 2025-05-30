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
package com.vertexcache.core.util.retry;

import com.vertexcache.common.log.LogHelper;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Default implementation of RetryPolicy that uses exponential backoff with optional jitter.
 *
 * Computes retry delays based on:
 * - A base delay (e.g., 100ms)
 * - A maximum cap to prevent unbounded wait times
 * - A JitterStrategy to introduce randomness if needed
 *
 * Useful for retrying transient operations such as network calls, alert dispatches,
 * or metric exports in a controlled and resilient manner.
 */
public class RetryBackoffService implements RetryPolicy {

    private final int baseDelayMs;
    private final int maxDelayMs;
    private final JitterStrategy strategy;

    public RetryBackoffService(int baseDelayMs, int maxDelayMs, JitterStrategy strategy) {
        this.baseDelayMs = baseDelayMs;
        this.maxDelayMs = maxDelayMs;
        this.strategy = strategy != null ? strategy : JitterStrategy.FULL;
    }

    @Override
    public int computeDelayForAttempt(int attempt) {
        int exponential = baseDelayMs * (1 << Math.min(attempt, 10));
        int capped = Math.min(exponential, maxDelayMs);

        int result;
        switch (strategy) {
            case NONE -> result = capped;

            case EQUAL -> {
                int half = capped / 2;
                result = half + ThreadLocalRandom.current().nextInt(-half / 2, half / 2 + 1);
                result = Math.max(1, Math.min(result, capped)); // clamp
            }

            case FULL -> {
                result = ThreadLocalRandom.current().nextInt(capped + 1);
            }

            default -> result = capped;
        }

        LogHelper.getInstance().logInfo(
                String.format("[RetryBackoff] attempt=%d, strategy=%s, computedDelay=%dms", attempt, strategy, result)
        );
        return result;
    }
}
