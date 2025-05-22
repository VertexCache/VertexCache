package com.vertexcache.module.metric.service;

import com.vertexcache.module.metric.model.MetricName;

public interface MetricCollector {
    void increment(MetricName name);
    void incrementBy(MetricName name, long count);
    void recordValue(MetricName name, long value);
    void setGauge(MetricName name, long value);
    long getCounter(MetricName name);
}

