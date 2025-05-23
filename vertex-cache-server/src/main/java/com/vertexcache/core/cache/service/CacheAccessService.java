package com.vertexcache.core.cache.service;

import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.cache.KeyPrefixer;
import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.module.auth.model.TenantId;
import com.vertexcache.module.metric.model.MetricKey;
import com.vertexcache.module.metric.model.MetricName;
import com.vertexcache.module.metric.service.MetricAccess;
import com.vertexcache.server.session.ClientSessionContext;
import com.vertexcache.common.log.LogHelper;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class CacheAccessService {

    private final Cache<Object, Object> cache;

    public CacheAccessService() throws VertexCacheException {
        try {
            this.cache = Cache.getInstance();
        } catch (Exception e) {
            throw new VertexCacheException("Failed to initialize CacheAccessService");
        }
    }

    // === SET ===

    public void put(ClientSessionContext session, String key, String value) {
        try {
            this.cache.put(KeyPrefixer.prefixKey(key, session), value);
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
                metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL);
                metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,this.cache.size());
            });
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(session, key, value)", session.getClientId(), key, ex);
        }
    }

    public void put(ClientSessionContext session, String key, String value, String idx1) {
        try {
            this.cache.put(KeyPrefixer.prefixKey(key, session), value, KeyPrefixer.prefixKey(idx1, session));
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
                metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL);
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX1);
                metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,this.cache.size());
            });
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(session, key, value, idx1)", session.getClientId(), key, ex);
        }
    }

    public void put(ClientSessionContext session, String key, String value, String idx1, String idx2) {
        try {
            this.cache.put(KeyPrefixer.prefixKey(key, session), value, KeyPrefixer.prefixKey(idx1, session), KeyPrefixer.prefixKey(idx2, session));
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
                metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL);
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX1);
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX2);
                metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,this.cache.size());
            });
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(session, key, value, idx1, idx2)", session.getClientId(), key, ex);
        }
    }

    public void put(TenantId tenant, String key, String value) {
        try {
            this.cache.put(tenant + "::" + key, value);
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL));
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
                metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL);
                metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,this.cache.size());
            });
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(tenant, key, value)", tenant, key, ex);
        }
    }

    public void put(TenantId tenant, String key, String value, String idx1) {
        try {
            this.cache.put(tenant + "::" + key, value, tenant + "::" + idx1);
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
                metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL);
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX1);
                metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,this.cache.size());
            });
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(tenant, key, value, idx1)", tenant, key, ex);
        }
    }

    public void put(TenantId tenant, String key, String value, String idx1, String idx2) {
        try {
            this.cache.put(tenant + "::" + key, value, tenant + "::" + idx1, tenant + "::" + idx2);
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
                metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL);
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX1);
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX2);
                metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,this.cache.size());
            });
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(tenant, key, value, idx1, idx2)", tenant, key, ex);
        }
    }


    // === GET ===

    public String get(ClientSessionContext session, String key) {
        return hitAndMissMetricTracking(key, (String) cache.get(KeyPrefixer.prefixKey(key, session)));
    }

    public String get(TenantId tenant, String key) {
        return hitAndMissMetricTracking(key, (String) cache.get(tenant + "::" + key));
    }

    private String hitAndMissMetricTracking(String key, String result) {
        Optional<MetricAccess> optionalMetricAccess = ModuleRegistry.getMetricAccessIfEnabled();
        if (optionalMetricAccess != null) {
            optionalMetricAccess.ifPresent(metricAccess -> metricAccess.getMetricCollector().increment(MetricName.CACHE_GET_TOTAL));
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> metrics.getHotKeyTracker().recordAccess(key));
            if (result != null) {
                optionalMetricAccess.ifPresent(metricAccess -> metricAccess.getMetricCollector().increment(MetricName.CACHE_HIT_COUNT));
            } else {
                optionalMetricAccess.ifPresent(metricAccess -> metricAccess.getMetricCollector().increment(MetricName.CACHE_MISS_COUNT));
            }
        }
        return result;
    }

    public String getBySecondaryIdx1(ClientSessionContext session, String idxKey) {
        ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX1));
        return (String) cache.getBySecondaryKeyIndexOne(KeyPrefixer.prefixKey(idxKey, session));
    }

    public String getBySecondaryIdx1(TenantId tenant, String idxKey) {
        ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX1));
        return (String) cache.getBySecondaryKeyIndexOne(tenant + "::" + idxKey);
    }

    public String getBySecondaryIdx2(ClientSessionContext session, String idxKey) {
        ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX2));
        return (String) cache.getBySecondaryKeyIndexTwo(KeyPrefixer.prefixKey(idxKey, session));
    }

    public String getBySecondaryIdx2(TenantId tenant, String idxKey) {
        ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX2));
        return (String) cache.getBySecondaryKeyIndexTwo(tenant + "::" + idxKey);
    }

    // === DELETE ===

    public void remove(ClientSessionContext session, String key) {
        cache.remove(KeyPrefixer.prefixKey(key, session));
        ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
            metrics.getMetricCollector().increment(MetricName.CACHE_DEL_TOTAL);
            metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,this.cache.size());
        });
    }

    public void remove(TenantId tenant, String key) {
        cache.remove(tenant + "::" + key);
        ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
            metrics.getMetricCollector().increment(MetricName.CACHE_DEL_TOTAL);
            metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,this.cache.size());
        });
    }

    // === Centralized Logging ===

    private void logAndRethrow(String op, Object context, String key, Exception ex) {
        String msg = String.format("[CacheAccess] Failed to %s. Context=%s, Key=%s, Error=%s",
                op, context != null ? context.toString() : "null", key, ex.getMessage());
        LogHelper.getInstance().logError(msg);
        throw new RuntimeException(msg, ex);
    }
}
