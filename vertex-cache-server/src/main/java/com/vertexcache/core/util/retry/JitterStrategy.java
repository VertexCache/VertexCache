package com.vertexcache.core.util.retry;

public enum JitterStrategy {
    NONE,      // Deterministic exponential delay
    FULL,      // Random between 0 and capped
    EQUAL      // Half of capped ± random jitter within half
}