package com.vertexcache.module.metric.service;

import com.vertexcache.module.metric.model.MetricKey;

public interface StructuredMetricCollector {
    void incrementCounter(MetricKey key);
    void incrementCounter(MetricKey key, long amount);
    void recordValue(MetricKey key, long value);
    void setGauge(MetricKey key, long value);
    void addTag(MetricKey key, String tag);
}