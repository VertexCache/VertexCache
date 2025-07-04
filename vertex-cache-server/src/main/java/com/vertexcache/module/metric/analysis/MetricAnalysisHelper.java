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
package com.vertexcache.module.metric.analysis;

import com.vertexcache.module.metric.core.MetricRegistry;
import com.vertexcache.module.metric.model.MetricKey;
import com.vertexcache.module.metric.model.MetricName;
import com.vertexcache.module.metric.model.MetricTag;
import com.vertexcache.module.metric.counter.ClientCommandCounters;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * MetricAnalysisHelper provides utility methods for interpreting and transforming
 * raw metric data within VertexCache. It supports functions such as calculating
 * hit ratios, TTL distributions, and other derived statistics used for diagnostics
 * and performance analysis.
 *
 * This helper class is used by the metrics subsystem to enrich exported or displayed
 * data, making it more actionable and insightful for operators and developers.
 */
public class MetricAnalysisHelper {

    private final MetricRegistry registry;
    private final HotKeyTracker hotKeyTracker;
    private final ClientCommandCounters clientCommandCounters;

    public MetricAnalysisHelper(
            MetricRegistry registry,
            HotKeyTracker hotKeyTracker,
            ClientCommandCounters clientCommandCounters
    ) {
        this.registry = registry;
        this.hotKeyTracker = hotKeyTracker;
        this.clientCommandCounters = clientCommandCounters;
    }

    public Map<String, Long> computeDerivedMetrics() {
        Map<String, Long> result = new HashMap<>();

        long hits = registry.getCounter(MetricName.CACHE_HIT_COUNT);
        long misses = registry.getCounter(MetricName.CACHE_MISS_COUNT);
        long total = hits + misses;
        long hitRatio = (total == 0) ? 0 : (hits * 100) / total;
        result.put("cache.hit.ratio.percent", hitRatio);

        long valueSizeSum = registry.getRecordedValueSum(MetricName.CACHE_VALUE_SIZE_BYTES);
        long setCount = registry.getCounter(MetricName.CACHE_SET_TOTAL);
        long avgSize = (setCount == 0) ? 0 : valueSizeSum / setCount;
        result.put("cache.avg.value.size.bytes", avgSize);

        return result;
    }

    public Map<String, Long> getHotKeyStats() {
        return hotKeyTracker.snapshot();
    }


    public Map<String, Long> getCommandCountsByClient() {
        return clientCommandCounters.snapshot();
    }

    public Map<String, Object> getTtlDistributionView() {
        Map<String, Object> view = new HashMap<>();
        ConcurrentMap<MetricKey, LongAdder> counters = registry.getKeyCounters();

        for (Map.Entry<MetricKey, LongAdder> entry : counters.entrySet()) {
            MetricKey key = entry.getKey();
            String metricValue = key.value(); // e.g., "cache.ttl.distribution.lt10s"

            if (metricValue.startsWith(MetricName.TTL_DISTRIBUTION_PREFIX)) {
                // Lookup MetricName by value
                MetricName name = MetricName.fromValue(metricValue);
                if (name != null && name.getTags().contains(MetricTag.TTL)) {
                    // Use the final segment of the TTL name for labeling
                    String[] parts = metricValue.split("\\.");
                    String bucket = parts[parts.length - 1]; // e.g., "lt10s"
                    view.put("ttl.bucket." + bucket, entry.getValue().sum());
                }
            }
        }

        return view;
    }
}
