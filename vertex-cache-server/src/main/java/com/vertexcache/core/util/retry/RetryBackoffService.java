package com.vertexcache.core.util.retry;

import com.vertexcache.common.log.LogHelper;

import java.util.concurrent.ThreadLocalRandom;

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
