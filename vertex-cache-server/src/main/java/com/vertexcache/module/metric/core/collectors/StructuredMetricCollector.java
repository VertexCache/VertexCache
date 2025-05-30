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
package com.vertexcache.module.metric.core.collectors;

import com.vertexcache.module.metric.model.MetricKey;

/**
 * StructuredMetricCollector defines the contract for collecting metrics in a structured,
 * tag-aware format within VertexCache. Implementations of this interface must support
 * capturing metrics with associated metadata such as tags and types.
 *
 * This abstraction enables consistent recording of multi-dimensional metrics, allowing
 * downstream systems to group, filter, and analyze metrics by attributes like operation type,
 * tenant, or client ID.
 */
public interface StructuredMetricCollector {
    void incrementCounter(MetricKey key);
    void incrementCounter(MetricKey key, long amount);
    void recordValue(MetricKey key, long value);
    void setGauge(MetricKey key, long value);
    void addTag(MetricKey key, String tag);
}