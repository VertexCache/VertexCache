package com.vertexcache.core.cache;

import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import com.vertexcache.core.cache.model.KeyPrefixer;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.module.auth.model.TenantId;
import com.vertexcache.module.metric.service.MetricAccess;
import com.vertexcache.server.session.ClientSessionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CacheAccessServiceTest {

    private CacheAccessService service;

    @BeforeEach
    void setUp() throws VertexCacheException {
        service = new CacheAccessService();
    }

    @Test
    void testPutAndGet_withSession() throws VertexCacheTypeException {
        ClientSessionContext session = mock(ClientSessionContext.class);
        when(session.getClientId()).thenReturn("test-client");

        try (MockedStatic<Cache> cacheMock = mockStatic(Cache.class);
             MockedStatic<KeyPrefixer> prefixerMock = mockStatic(KeyPrefixer.class)) {

            String prefixedKey = "tenant::key";
            String value = "value";

            Cache mockCache = mock(Cache.class);
            cacheMock.when(Cache::getInstance).thenReturn(mockCache);
            prefixerMock.when(() -> KeyPrefixer.prefixKey("key", session)).thenReturn(prefixedKey);

            service.put(session, "key", value);
            verify(mockCache).put(prefixedKey, value);
        }
    }

    @Test
    void testRemove_withTenant() throws VertexCacheTypeException {
        TenantId tenantId = new TenantId("tenant");
        String fullKey = "tenant::key";

        try (MockedStatic<Cache> cacheMock = mockStatic(Cache.class)) {
            Cache mockCache = mock(Cache.class);
            cacheMock.when(Cache::getInstance).thenReturn(mockCache);

            service.remove(tenantId, "key");

            verify(mockCache).remove(fullKey);
        }
    }

    @Test
    void testSweepOrphanedIndexEntries_handlesOrphansGracefully() throws VertexCacheException {
        try (MockedStatic<Cache> cacheMock = mockStatic(Cache.class)) {
            Cache mockCache = mock(Cache.class);
            cacheMock.when(Cache::getInstance).thenReturn(mockCache);

            // Simulate orphaned index logic here as needed
            when(mockCache.getReverseIndex()).thenReturn(Map.of());
            when(mockCache.getReadOnlySecondaryIndexOne()).thenReturn(Map.of());
            when(mockCache.getReadOnlySecondaryIndexTwo()).thenReturn(Map.of());

            assertDoesNotThrow(() -> service.sweepOrphanedIndexEntries());
        }
    }

}

