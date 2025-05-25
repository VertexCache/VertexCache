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

    private static final int CHURN_WINDOW_SEC = 60;
    private static final int BASELINE_THRESHOLD = 500;
    private static final int MAX_THRESHOLD = 100_000;

    private final Set<String> recentKeys = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public KeyChurnAlertService() throws VertexCacheException {
        super();
    }

    @Override
    public void start() {
        executor.scheduleAtFixedRate(this::resetWindow, CHURN_WINDOW_SEC, CHURN_WINDOW_SEC, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        executor.shutdownNow();
    }

    public void onKeySet(String key) throws VertexCacheTypeException {
        recentKeys.add(key);
        if (recentKeys.size() >= getDynamicThreshold()) {
            triggerAlert();
        }
    }

    private int getDynamicThreshold() throws VertexCacheTypeException {
        int size = this.getCacheAccessService().getKeyCount();
        long dynamic = Math.max(BASELINE_THRESHOLD, size / 10L);
        return (int) Math.min(dynamic, MAX_THRESHOLD); // Always capped to prevent memory blowout
    }

    private void triggerAlert() throws VertexCacheTypeException {
        int dynamicThreshold = getDynamicThreshold();
        int recentKeyCount = recentKeys.size();
        long cacheSize = this.getCacheAccessService().getKeyCount();

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

        recentKeys.clear(); // reset after alert
    }

    private void resetWindow() {
        recentKeys.clear();
    }
}
