package com.vertexcache.core.cache.impl;


import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class CacheClockTest {

    private CacheClock<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new CacheClock<>(4); // small capacity for eviction testing
    }

    @Test
    void testPutAndGet() throws VertexCacheTypeException {
        cache.put("a", "alpha");
        cache.put("b", "beta");

        assertEquals("alpha", cache.get("a"));
        assertEquals("beta", cache.get("b"));
    }

    @Test
    void testEviction() throws VertexCacheTypeException {
        cache.put("a", "alpha");
        cache.put("b", "beta");
        cache.put("c", "charlie");
        cache.put("d", "delta");

        // Trigger eviction
        cache.put("e", "echo");

        int remaining = 0;
        for (String key : new String[]{"a", "b", "c", "d", "e"}) {
            if (cache.get(key) != null) remaining++;
        }

        assertEquals(4, remaining, "Only 4 entries should remain due to CLOCK eviction");
    }

    @Test
    void testRemove() throws VertexCacheTypeException {
        cache.put("x", "x-ray");
        cache.remove("x");
        assertNull(cache.get("x"));
    }

    @Test
    void testSecondaryKeyTracking() throws VertexCacheTypeException {
        cache.put("uid123", "session", "email@site.com", "user-id");

        assertEquals("session", cache.getBySecondaryKeyIndexOne("email@site.com"));
        assertEquals("session", cache.getBySecondaryKeyIndexTwo("user-id"));

        cache.remove("uid123");

        assertNull(cache.getBySecondaryKeyIndexOne("email@site.com"));
        assertNull(cache.getBySecondaryKeyIndexTwo("user-id"));
    }

    @Test
    void testConcurrencySafety() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        int operations = 1000;
        CountDownLatch latch = new CountDownLatch(operations);

        for (int i = 0; i < operations; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    String key = "key" + (id % 10);
                    cache.put(key, "value" + id, "idx1-" + id, "idx2-" + id);
                    cache.get(key);
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(cache.size() <= 10, "Cache size should be within CLOCK policy bounds");
    }
}
