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

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.alert.model.AlertEvent;
import com.vertexcache.module.alert.model.AlertEventType;

import java.util.HashMap;
import java.util.Map;

/**
 * Alert service that detects anomalies in hot key access patterns.
 *
 * Runs periodically (every 10 seconds) to compare current top key hit counts
 * against the previous snapshot. Triggers an alert if a key's hits exceed
 * defined thresholds (e.g., a 5x increase or 1000+ absolute increase above 500 hits).
 *
 * When enabled, dispatches an AlertEvent and logs a warning with anomaly details.
 */
public class HotKeyAnomalyAlertService extends BaseAlertService {

    private Map<String, Long> previousSnapshot = new HashMap<>();

    public HotKeyAnomalyAlertService() throws VertexCacheException {
        super("HotKeyAnomaly", 10); // runs every 10 seconds
    }

    public void evaluate() {
        Map<String, Long> currentTop = this.getMetricModule().getMetricAccess().getHotKeyTracker().getTopKeys(10);

        for (Map.Entry<String, Long> entry : currentTop.entrySet()) {
            String key = entry.getKey();
            long now = entry.getValue();
            long before = previousSnapshot.getOrDefault(key, 0L);

            if (now >= 500 && (now > before * 5 || now - before > 1000)) {
                Map<String, Object> details = Map.of(
                        "key", key,
                        "current_hits", now,
                        "previous_hits", before,
                        "increase_pct", before == 0 ? 1000 : (int) (((double) (now - before) / before) * 100)
                );
                if(Config.getInstance().getAlertConfigLoader().isEnableAlerting()) {
                    this.getAlertModule().dispatch(new AlertEvent(
                            AlertEventType.HOT_KEY_ANOMALY,
                            Config.getInstance().getCoreConfigLoader().getLocalNodeId(),
                            details
                    ));
                }
                String logMessage = String.format(
                        "[HOT_KEY_ANOMALY] key='%s', current_hits=%d, previous_hits=%d, increase_pct=%d%%",
                        key, now, before, (before == 0 ? 1000 : (int) (((double) (now - before) / before) * 100))
                );
                LogHelper.getInstance().logWarn(logMessage);
            }
        }

        previousSnapshot = currentTop;
    }
}
