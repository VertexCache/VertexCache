package com.vertexcache.core.util.retry;

public interface RetryPolicy {
    int computeDelayForAttempt(int attempt);
}