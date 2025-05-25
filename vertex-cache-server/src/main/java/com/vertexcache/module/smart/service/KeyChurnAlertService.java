package com.vertexcache.module.smart.service;

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

            this.getAlertModule().dispatch(new AlertEvent(
                    AlertEventType.KEY_CHURN,
                    Config.getInstance().getCoreConfigLoader().getLocalNodeId(),
                    details
            ));
        }

        recentKeys.clear();
    }
}
