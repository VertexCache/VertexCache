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
package com.vertexcache.core.cache;

import com.vertexcache.core.cache.model.CacheIndexRef;
import com.vertexcache.core.cache.model.KeyPrefixer;
import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.module.auth.model.TenantId;
import com.vertexcache.module.metric.model.MetricName;
import com.vertexcache.module.metric.service.MetricAccess;
import com.vertexcache.server.session.ClientSessionContext;
import com.vertexcache.common.log.LogHelper;

import java.util.Map;
import java.util.Optional;

public class CacheAccessService {

    public CacheAccessService() throws VertexCacheException {
    }

    // === SET ===

    public void put(ClientSessionContext session, String key, String value) {
        try {
            Cache.getInstance().put(KeyPrefixer.prefixKey(key, session), value);
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
                metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL);
                try {
                    metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,Cache.getInstance().size());
                } catch (VertexCacheTypeException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(session, key, value)", session.getClientId(), key, ex);
        }
    }

    public void put(ClientSessionContext session, String key, String value, String idx1) {
        try {
            Cache.getInstance().put(KeyPrefixer.prefixKey(key, session), value, KeyPrefixer.prefixKey(idx1, session));
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
                metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL);
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX1);
                try {
                    metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,Cache.getInstance().size());
                } catch (VertexCacheTypeException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(session, key, value, idx1)", session.getClientId(), key, ex);
        }
    }

    public void put(ClientSessionContext session, String key, String value, String idx1, String idx2) {
        try {
            Cache.getInstance().put(KeyPrefixer.prefixKey(key, session), value, KeyPrefixer.prefixKey(idx1, session), KeyPrefixer.prefixKey(idx2, session));
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
                metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL);
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX1);
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX2);
                try {
                    metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,Cache.getInstance().size());
                } catch (VertexCacheTypeException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(session, key, value, idx1, idx2)", session.getClientId(), key, ex);
        }
    }

    public void put(TenantId tenant, String key, String value) {
        try {
            Cache.getInstance().put(tenant + "::" + key, value);
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL));
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
                metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL);
                try {
                    metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,Cache.getInstance().size());
                } catch (VertexCacheTypeException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(tenant, key, value)", tenant, key, ex);
        }
    }

    public void put(TenantId tenant, String key, String value, String idx1) {
        try {
            Cache.getInstance().put(tenant + "::" + key, value, tenant + "::" + idx1);
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
                metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL);
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX1);
                try {
                    metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,Cache.getInstance().size());
                } catch (VertexCacheTypeException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(tenant, key, value, idx1)", tenant, key, ex);
        }
    }

    public void put(TenantId tenant, String key, String value, String idx1, String idx2) {
        try {
            Cache.getInstance().put(tenant + "::" + key, value, tenant + "::" + idx1, tenant + "::" + idx2);
            ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
                metrics.getMetricCollector().increment(MetricName.CACHE_SET_TOTAL);
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX1);
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX2);
                try {
                    metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,Cache.getInstance().size());
                } catch (VertexCacheTypeException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(tenant, key, value, idx1, idx2)", tenant, key, ex);
        }
    }


    // === GET ===

    public String get(ClientSessionContext session, String key) throws VertexCacheTypeException {
        return hitAndMissMetricTracking(key, (String) Cache.getInstance().get(KeyPrefixer.prefixKey(key, session)));
    }

    public String get(TenantId tenant, String key) throws VertexCacheTypeException {
        return hitAndMissMetricTracking(key, (String) Cache.getInstance().get(tenant + "::" + key));
    }

    private String hitAndMissMetricTracking(String key, String result) {
        Optional<MetricAccess> optionalMetricAccess = ModuleRegistry.getMetricAccessIfEnabled();
        if (optionalMetricAccess.isPresent()) {
            MetricAccess metrics = optionalMetricAccess.get();
            metrics.getMetricCollector().increment(MetricName.CACHE_GET_TOTAL);
            metrics.getHotKeyTracker().recordAccess(key);

            if (result != null) {
                metrics.getMetricCollector().increment(MetricName.CACHE_HIT_COUNT);
            } else {
                metrics.getMetricCollector().increment(MetricName.CACHE_MISS_COUNT);
            }
        }
        return result;
    }

    public String getBySecondaryIdx1(ClientSessionContext session, String idxKey) throws VertexCacheTypeException {
        return this.getBySecondaryIdx1(KeyPrefixer.prefixKey(idxKey, session));
    }

    public String getBySecondaryIdx1(TenantId tenant, String idxKey) throws VertexCacheTypeException {
        return this.getBySecondaryIdx1(tenant + "::" + idxKey);
    }

    private String getBySecondaryIdx1(String key) throws VertexCacheTypeException {

        // Look up the primary key using the reverse index
        String primaryKey = (String) Cache.getInstance().getReadOnlySecondaryIndexOne().get(key);
        if (primaryKey == null) {
            return null;
        }

        // Record metric for index lookup usage
        ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics ->
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX1)
        );

        // Return the value associated with the resolved primary key
        return (String) Cache.getInstance().get(primaryKey);
    }

    public String getBySecondaryIdx2(ClientSessionContext session, String idxKey) throws VertexCacheTypeException {
        return this.getBySecondaryIdx2(KeyPrefixer.prefixKey(idxKey, session));
    }

    public String getBySecondaryIdx2(TenantId tenant, String idxKey) throws VertexCacheTypeException {
        return this.getBySecondaryIdx2(tenant + "::" + idxKey);
    }

    private String getBySecondaryIdx2(String key) throws VertexCacheTypeException {

        // Look up the primary key using the reverse index
        String primaryKey = (String) Cache.getInstance().getReadOnlySecondaryIndexTwo().get(key);
        if (primaryKey == null) {
            return null;
        }

        // Record metric for index lookup usage
        ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics ->
                metrics.getMetricCollector().increment(MetricName.CACHE_INDEX_USAGE_IDX2)
        );

        // Return the value associated with the resolved primary key
        return (String) Cache.getInstance().get(primaryKey);
    }

    // === DELETE ===

    public void remove(ClientSessionContext session, String key) throws VertexCacheTypeException {
        this.remove(KeyPrefixer.prefixKey(key, session));
    }

    public void remove(TenantId tenant, String key) throws VertexCacheTypeException {
        this.remove(tenant + "::" + key);
    }

    private void remove(String key) throws VertexCacheTypeException {
        Cache.getInstance().remove( key);
        ModuleRegistry.getMetricAccessIfEnabled().ifPresent(metrics -> {
            metrics.getMetricCollector().increment(MetricName.CACHE_DEL_TOTAL);
            try {
                metrics.getMetricCollector().setGauge(MetricName.CACHE_KEY_COUNT,Cache.getInstance().size());
            } catch (VertexCacheTypeException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // === Centralized Logging ===

    private void logAndRethrow(String op, Object context, String key, Exception ex) {
        String msg = String.format("[CacheAccess] Failed to %s. Context=%s, Key=%s, Error=%s",
                op, context != null ? context.toString() : "null", key, ex.getMessage());
        LogHelper.getInstance().logError(msg);
        throw new RuntimeException(msg, ex);
    }

    // ==== Cache Utils ===

    public void sweepOrphanedIndexEntries() throws VertexCacheException {
        try {
            Map<Object, CacheIndexRef> reverseIndex = Cache.getInstance().getReverseIndex();
            Map<Object, Object> idx1Map = Cache.getInstance().getReadOnlySecondaryIndexOne();
            Map<Object, Object> idx2Map = Cache.getInstance().getReadOnlySecondaryIndexTwo();

            for (Map.Entry<Object, CacheIndexRef> entry : reverseIndex.entrySet()) {
                Object primaryKey = entry.getKey();
                CacheIndexRef ref = entry.getValue();

                if (!Cache.getInstance().containsKey(primaryKey)) {
                    if (ref.getIdx1() != null) {
                        idx1Map.remove(ref.getIdx1(), primaryKey);
                    }
                    if (ref.getIdx2() != null) {
                        idx2Map.remove(ref.getIdx2(), primaryKey);
                    }

                    reverseIndex.remove(primaryKey);
                }
            }
        } catch (Exception e) {
            throw new VertexCacheException("sweepOrphanedIndexEntries Failed");
        }
    }

    public int getKeyCount() throws VertexCacheTypeException {
        return Cache.getInstance().size();
    }

}
