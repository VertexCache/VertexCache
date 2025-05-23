package com.vertexcache.module.metric.service;

import com.vertexcache.module.metric.model.MetricKey;
import com.vertexcache.module.metric.model.MetricName;

public class DefaultMetricCollector implements MetricCollector, StructuredMetricCollector {

    private final MetricRegistry registry;

    public DefaultMetricCollector(MetricRegistry registry) {
        this.registry = registry;
    }

    // --- MetricCollector (MetricName) ---
    @Override
    public void increment(MetricName name) {registry.increment(name);}

    @Override
    public void incrementBy(MetricName name, long count) {
        registry.incrementBy(name, count);
    }

    @Override
    public void recordValue(MetricName name, long value) {
        registry.recordValue(name, value);
    }

    @Override
    public void setGauge(MetricName name, long value) {
        registry.setGauge(name, value);
    }

    @Override
    public long getCounter(MetricName name) {return registry.getCounter(name);}

    @Override
    public long getGauge(MetricName name) {return registry.getGauge(name);}


    // --- StructuredMetricCollector (MetricKey) ---
    @Override
    public void incrementCounter(MetricKey key) {registry.incrementCounter(key);}

    @Override
    public void incrementCounter(MetricKey key, long amount) {
        registry.incrementCounter(key, amount);
    }

    @Override
    public void recordValue(MetricKey key, long value) {
        registry.recordValue(key, value);
    }

    @Override
    public void setGauge(MetricKey key, long value) {
        registry.setGauge(key, value);
    }

    @Override
    public void addTag(MetricKey key, String tag) {
        registry.addTag(key, tag);
    }
}
