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
import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.alert.model.AlertEvent;
import com.vertexcache.module.alert.model.AlertEventType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class KeyChurnAlertService extends BaseAlertService {

    private static final int CHURN_WINDOW_SEC = 10;
    private static final int BASE_THRESHOLD = 100;
    private static final int MAX_CACHE_SIZE = 1000000;
    private static final long ALERT_COOLDOWN_MS = 30_000;

    private final Set<String> recentKeys = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private long lastAlertTime = 0;

    public KeyChurnAlertService() throws VertexCacheException {
        super("KeyChurn", CHURN_WINDOW_SEC);
    }

    @Override
    protected void evaluate() throws VertexCacheTypeException {
        int recentKeyCount = recentKeys.size();
        int cacheSize = this.getCacheAccessService().getKeyCount();

        if (cacheSize <= 0) {
            recentKeys.clear();
            return;
        }

        int dynamicThreshold = Math.min(BASE_THRESHOLD + (int)(cacheSize * 0.01), MAX_CACHE_SIZE);
        long now = System.currentTimeMillis();

        if (recentKeyCount >= dynamicThreshold && (now - lastAlertTime) >= ALERT_COOLDOWN_MS) {
            lastAlertTime = now;

            Map<String, Object> details = new HashMap<>();
            details.put("message", "High key churn: " + recentKeyCount + " unique keys in " + CHURN_WINDOW_SEC + " seconds.");
            details.put("unique_key_count", recentKeyCount);
            details.put("threshold", dynamicThreshold);
            details.put("window_sec", CHURN_WINDOW_SEC);
            details.put("total_cache_keys", cacheSize);

            if(Config.getInstance().getAlertConfigLoader().isEnableAlerting()) {
                this.getAlertModule().dispatch(new AlertEvent(
                        AlertEventType.KEY_CHURN,
                        Config.getInstance().getCoreConfigLoader().getLocalNodeId(),
                        details
                ));
            }
            String logMessage = String.format(
                    "[KEY_CHURN] Detected %d unique keys in %d seconds (threshold=%d, total_cache_keys=%d)",
                    recentKeyCount, CHURN_WINDOW_SEC, dynamicThreshold, cacheSize
            );
            LogHelper.getInstance().logWarn(logMessage);
        }

        recentKeys.clear();
    }
}
