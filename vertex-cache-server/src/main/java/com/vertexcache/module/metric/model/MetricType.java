package com.vertexcache.module.metric.model;

public enum MetricType {
    COUNTER,     // Increments only
    GAUGE,       // Can go up/down (e.g., current key count)
    RECORDER,    // Accumulates values (e.g., value sizes)
    DERIVED      // Computed from other metrics (e.g., avg value size)
}
