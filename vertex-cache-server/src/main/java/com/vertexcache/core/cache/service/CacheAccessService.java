package com.vertexcache.core.cache.service;

import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.cache.KeyPrefixer;
import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import com.vertexcache.module.auth.model.TenantId;
import com.vertexcache.server.session.ClientSessionContext;
import com.vertexcache.common.log.LogHelper;

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
            cache.put(KeyPrefixer.prefixKey(key, session), value);
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(session, key, value)", session.getClientId(), key, ex);
        }
    }

    public void put(ClientSessionContext session, String key, String value, String idx1) {
        try {
            cache.put(KeyPrefixer.prefixKey(key, session), value,
                    KeyPrefixer.prefixKey(idx1, session));
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(session, key, value, idx1)", session.getClientId(), key, ex);
        }
    }

    public void put(ClientSessionContext session, String key, String value, String idx1, String idx2) {
        try {
            cache.put(KeyPrefixer.prefixKey(key, session), value,
                    KeyPrefixer.prefixKey(idx1, session),
                    KeyPrefixer.prefixKey(idx2, session));
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(session, key, value, idx1, idx2)", session.getClientId(), key, ex);
        }
    }

    public void put(TenantId tenant, String key, String value) {
        try {
            cache.put(tenant + "::" + key, value);
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(tenant, key, value)", tenant, key, ex);
        }
    }

    public void put(TenantId tenant, String key, String value, String idx1) {
        try {
            cache.put(tenant + "::" + key, value, tenant + "::" + idx1);
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(tenant, key, value, idx1)", tenant, key, ex);
        }
    }

    public void put(TenantId tenant, String key, String value, String idx1, String idx2) {
        try {
            cache.put(tenant + "::" + key, value, tenant + "::" + idx1, tenant + "::" + idx2);
        } catch (VertexCacheTypeException ex) {
            logAndRethrow("put(tenant, key, value, idx1, idx2)", tenant, key, ex);
        }
    }

    // === GET ===

    public String get(ClientSessionContext session, String key) {
        return (String) cache.get(KeyPrefixer.prefixKey(key, session));
    }

    public String get(TenantId tenant, String key) {
        return (String) cache.get(tenant + "::" + key);
    }

    public String getBySecondaryIdx1(ClientSessionContext session, String idxKey) {
        return (String) cache.getBySecondaryKeyIndexOne(KeyPrefixer.prefixKey(idxKey, session));
    }

    public String getBySecondaryIdx1(TenantId tenant, String idxKey) {
        return (String) cache.getBySecondaryKeyIndexOne(tenant + "::" + idxKey);
    }

    public String getBySecondaryIdx2(ClientSessionContext session, String idxKey) {
        return (String) cache.getBySecondaryKeyIndexTwo(KeyPrefixer.prefixKey(idxKey, session));
    }

    public String getBySecondaryIdx2(TenantId tenant, String idxKey) {
        return (String) cache.getBySecondaryKeyIndexTwo(tenant + "::" + idxKey);
    }

    // === DELETE ===

    public void remove(ClientSessionContext session, String key) {
        cache.remove(KeyPrefixer.prefixKey(key, session));
    }

    public void remove(TenantId tenant, String key) {
        cache.remove(tenant + "::" + key);
    }

    // === Centralized Logging ===

    private void logAndRethrow(String op, Object context, String key, Exception ex) {
        String msg = String.format("[CacheAccess] Failed to %s. Context=%s, Key=%s, Error=%s",
                op, context != null ? context.toString() : "null", key, ex.getMessage());
        LogHelper.getInstance().logError(msg);
        throw new RuntimeException(msg, ex);
    }
}
