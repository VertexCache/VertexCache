package com.vertexcache.core.cache.impl;

import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class CacheRandomTest {

    private CacheRandom<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new CacheRandom<>(3); // Small capacity to force eviction
    }

    @Test
    void testPutAndGet() throws VertexCacheTypeException {
        cache.put("a", "apple");
        cache.put("b", "banana");

        assertEquals("apple", cache.get("a"));
        assertEquals("banana", cache.get("b"));
    }

    @Test
    void testRandomEviction() throws VertexCacheTypeException {
        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");

        // All 3 should be present
        assertNotNull(cache.get("a"));
        assertNotNull(cache.get("b"));
        assertNotNull(cache.get("c"));

        // Add 4th to trigger eviction
        cache.put("d", "D");

        int present = 0;
        for (String k : new String[]{"a", "b", "c", "d"}) {
            if (cache.get(k) != null) present++;
        }

        assertEquals(3, present, "One key should have been randomly evicted");
    }

    @Test
    void testRemove() throws VertexCacheTypeException {
        cache.put("x", "value-x");
        cache.remove("x");
        assertNull(cache.get("x"));
    }

    @Test
    void testSecondaryIndexTracking() throws VertexCacheTypeException {
        cache.put("k", "v", "email@example.com", "user-id-999");

        assertEquals("v", cache.getBySecondaryKeyIndexOne("email@example.com"));
        assertEquals("v", cache.getBySecondaryKeyIndexTwo("user-id-999"));

        cache.remove("k");

        assertNull(cache.getBySecondaryKeyIndexOne("email@example.com"));
        assertNull(cache.getBySecondaryKeyIndexTwo("user-id-999"));
    }

    @Test
    void testConcurrencySafety() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(300);

        for (int i = 0; i < 300; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    String key = "key" + (id % 10);
                    cache.put(key, "val" + id, "idx1-" + id, "idx2-" + id);
                    cache.get(key);
                    cache.remove("nonexistent-" + id); // should not throw
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(cache.size() <= 10);
    }
}
