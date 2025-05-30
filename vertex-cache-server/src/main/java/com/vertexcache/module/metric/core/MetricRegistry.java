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
package com.vertexcache.module.metric.core;

import com.vertexcache.module.metric.model.MetricKey;
import com.vertexcache.module.metric.model.MetricName;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * MetricRegistry acts as the central repository for all registered metrics within VertexCache.
 * It manages metric definitions, lookup, and access, providing a unified interface for
 * collectors, exporters, and analysis tools to interact with metric data.
 *
 * This class ensures consistency across the metrics subsystem by maintaining a catalog
 * of known metric names, types, tags, and collection strategies.
 */
public class MetricRegistry {

    // --- Static/global metric storage (MetricName) ---
    private final ConcurrentMap<MetricName, LongAdder> counters = new ConcurrentHashMap<>();
    private final ConcurrentMap<MetricName, LongAdder> recordedValues = new ConcurrentHashMap<>();
    private final ConcurrentMap<MetricName, AtomicLong> gauges = new ConcurrentHashMap<>();

    // --- Structured/dynamic metric storage (MetricKey) ---
    private final ConcurrentMap<MetricKey, LongAdder> keyCounters = new ConcurrentHashMap<>();
    private final ConcurrentMap<MetricKey, LongAdder> keyRecordedValues = new ConcurrentHashMap<>();
    private final ConcurrentMap<MetricKey, AtomicLong> keyGauges = new ConcurrentHashMap<>();
    private final ConcurrentMap<MetricKey, Set<String>> tagMap = new ConcurrentHashMap<>();

    // --- MetricName operations ---

    public void increment(MetricName name) {
        incrementBy(name, 1);
    }

    public void incrementBy(MetricName name, long count) {
        LongAdder adder = counters.computeIfAbsent(name, n -> new LongAdder());
        adder.add(count);
    }

    public void recordValue(MetricName name, long value) {
        recordedValues.computeIfAbsent(name, n -> new LongAdder()).add(value);
    }

    public void setGauge(MetricName name, long value) {
        gauges.computeIfAbsent(name, n -> new AtomicLong()).set(value);
    }

    // --- MetricKey operations ---

    public void incrementCounter(MetricKey key) {
        incrementCounter(key, 1);
    }

    public void incrementCounter(MetricKey key, long amount) {
        keyCounters.computeIfAbsent(key, k -> new LongAdder()).add(amount);
    }

    public void recordValue(MetricKey key, long value) {
        keyRecordedValues.computeIfAbsent(key, k -> new LongAdder()).add(value);
    }

    public void setGauge(MetricKey key, long value) {
        keyGauges.computeIfAbsent(key, k -> new AtomicLong()).set(value);
    }

    public void addTag(MetricKey key, String tag) {
        tagMap.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(tag);
    }

    // --- Optional accessors (used by MetricAnalysisHelper etc.) ---

    public long getCounter(MetricName name) {
        LongAdder adder = counters.get(name);
        return (adder != null) ? adder.sum() : 0L;
    }

    public long getRecordedValueSum(MetricName name) {
        LongAdder adder = recordedValues.get(name);
        return (adder != null) ? adder.sum() : 0L;
    }

    public long getGauge(MetricName name) {
        AtomicLong gauge = gauges.get(name);
        return (gauge != null) ? gauge.get() : 0L;
    }

    public ConcurrentMap<MetricKey, LongAdder> getKeyCounters() {
        return keyCounters;
    }
}
