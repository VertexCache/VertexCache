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
