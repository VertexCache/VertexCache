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
package com.vertexcache.module.smart.service;

import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.alert.model.AlertEvent;
import com.vertexcache.module.alert.model.AlertEventType;
import com.vertexcache.module.metric.model.MetricViewKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * HotKeyWatcherService is a background service that monitors cache access patterns
 * to identify frequently accessed ("hot") keys over time.
 *
 * It periodically scans internal hit counters or frequency tracking structures to determine
 * the top-N most accessed keys. These keys are then recorded or exposed via metrics for
 * diagnostic, monitoring, or optimization purposes (e.g., indexing, preloading, or alerting).
 *
 * This service is designed to be lightweight and efficient, with the following characteristics:
 *
 * - Operates on a configurable interval (e.g., every 30 seconds or 5 minutes)
 * - Extracts top-N hot keys based on access count deltas or time-windowed counters
 * - Has negligible impact on main cache performance due to asynchronous execution
 * - Does not interfere with core get/set/del logic
 *
 * Typical use cases include:
 * - Detecting misuse or overuse of specific keys
 * - Highlighting which entries dominate cache workload
 * - Feeding alert systems or export modules with hot key statistics
 *
 * The watcher should be started on cache initialization and gracefully shut down during cleanup.
 * It works best when paired with efficient counter management and optionally integrates
 * with metrics reporting systems (e.g., Prometheus).
 */
public final class HotKeyWatcherAlertService extends BaseAlertService {

    public HotKeyWatcherAlertService() throws VertexCacheException {
        super("HotKeyWatcher", 10); // Run every 10 seconds
    }

    @Override
    protected void evaluate() {
        Map<String, Object> hotKeys = this.getMetricModule().getMetricAccess().getHotKeysView();
        LongAdder adder = (LongAdder) hotKeys.get(MetricViewKey.COMMAND_GET_TOTAL);
        long totalGet = adder == null ? 0 : adder.longValue();

        if (hotKeys.isEmpty() || totalGet == 0) return;

        Map.Entry<String, Object> top = hotKeys.entrySet().iterator().next();
        String key = top.getKey();
        long hits = (long) top.getValue();
        double ratio = (100.0 * hits) / totalGet;

        if (ratio >= 80.0) {
            String msg = "[HotKeyWatcher] Key '" + key + "' has " + hits + " hits (" + String.format("%.2f", ratio) + "% of all GETs)";
            Map<String, Object> details = new HashMap<>();
            details.put("message", "Key '" + key + "' accounted for " + String.format("%.2f", ratio) + "% of GET traffic.");
            details.put("key", key);
            details.put("hit_count", hits);
            details.put("hit_ratio_percent", String.format("%.2f", ratio));
            details.put("total_get_requests", totalGet);

            this.getAlertModule().dispatch(new AlertEvent(
                    AlertEventType.HOT_KEY_ALERT,
                    Config.getInstance().getCoreConfigLoader().getLocalNodeId(),
                    details
            ));
        }
    }
}
