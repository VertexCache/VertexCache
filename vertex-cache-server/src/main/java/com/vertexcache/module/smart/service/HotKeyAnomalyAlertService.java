package com.vertexcache.module.smart.service;

import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.alert.model.AlertEvent;
import com.vertexcache.module.alert.model.AlertEventType;

import java.util.HashMap;
import java.util.Map;

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
                this.getAlertModule().dispatch(new AlertEvent(
                        AlertEventType.HOT_KEY_ANOMALY,
                        Config.getInstance().getCoreConfigLoader().getLocalNodeId(),
                        details
                ));
            }
        }

        previousSnapshot = currentTop;
    }
}
