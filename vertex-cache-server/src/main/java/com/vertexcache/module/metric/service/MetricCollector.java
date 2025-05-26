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

import com.vertexcache.module.metric.model.MetricName;

public interface MetricCollector {
    void increment(MetricName name);
    void incrementBy(MetricName name, long count);
    void recordValue(MetricName name, long value);
    void setGauge(MetricName name, long value);

    long getCounter(MetricName name);
    long getGauge(MetricName name);
}

