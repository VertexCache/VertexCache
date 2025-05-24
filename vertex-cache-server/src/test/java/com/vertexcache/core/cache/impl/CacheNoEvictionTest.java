package com.vertexcache.core.cache.impl;


import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class CacheNoEvictionTest {

    private CacheNoEviction<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new CacheNoEviction<>();
    }

    @Test
    void testBasicPutAndGet() throws VertexCacheTypeException {
        cache.put("a", "apple");
        cache.put("b", "banana");

        assertEquals("apple", cache.get("a"));
        assertEquals("banana", cache.get("b"));
    }

    @Test
    void testAllKeysAreRetainedRegardlessOfCapacity() throws VertexCacheTypeException {
        cache.put("k1", "v1");
        cache.put("k2", "v2");
        cache.put("k3", "v3");
        cache.put("k4", "v4"); // Over the initial capacity

        assertEquals("v1", cache.get("k1"));
        assertEquals("v2", cache.get("k2"));
        assertEquals("v3", cache.get("k3"));
        assertEquals("v4", cache.get("k4"));

        assertEquals(4, cache.size());
    }

    @Test
    void testRemove() throws VertexCacheTypeException {
        cache.put("remove-key", "value");
        cache.remove("remove-key");

        assertNull(cache.get("remove-key"));
    }

    @Test
    void testSecondaryIndexCleanup() throws VertexCacheTypeException {
        cache.put("u", "user-session", "user@example.com", "user-123");

        assertEquals("user-session", cache.getBySecondaryKeyIndexOne("user@example.com"));
        assertEquals("user-session", cache.getBySecondaryKeyIndexTwo("user-123"));

        cache.remove("u");

        assertNull(cache.getBySecondaryKeyIndexOne("user@example.com"));
        assertNull(cache.getBySecondaryKeyIndexTwo("user-123"));
    }

    @Test
    void testConcurrencySafety() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(300);

        for (int i = 0; i < 300; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    String key = "k" + (id % 50);
                    cache.put(key, "v" + id, "email" + id, "uid" + id);
                    cache.get(key);
                    cache.remove("ghost-" + id); // no-op, shouldn't throw
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(cache.size() <= 50);
    }
}


