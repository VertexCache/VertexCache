package com.vertexcache.core.cache.impl;

import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class CacheLRUTest {

    private CacheLRU<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new CacheLRU<>(3); // small enough to trigger evictions
    }

    @Test
    void testPutAndGet() throws VertexCacheTypeException {
        cache.put("one", "1");
        cache.put("two", "2");

        assertEquals("1", cache.get("one"));
        assertEquals("2", cache.get("two"));
    }

    @Test
    void testEvictionLRU() throws VertexCacheTypeException {
        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");

        // Access 'a' to make it most recently used
        cache.get("a");

        // Add one more to trigger eviction
        cache.put("d", "D");

        // 'b' should be evicted (least recently used)
        assertNotNull(cache.get("a"));
        assertNull(cache.get("b"));
        assertNotNull(cache.get("c"));
        assertNotNull(cache.get("d"));
    }

    @Test
    void testRemove() throws VertexCacheTypeException {
        cache.put("x", "xray");
        cache.remove("x");
        assertNull(cache.get("x"));
    }

    @Test
    void testSecondaryIndexUsage() throws VertexCacheTypeException {
        cache.put("k", "v", "email@abc.com", "user42");

        assertEquals("v", cache.getBySecondaryKeyIndexOne("email@abc.com"));
        assertEquals("v", cache.getBySecondaryKeyIndexTwo("user42"));

        cache.remove("k");

        assertNull(cache.getBySecondaryKeyIndexOne("email@abc.com"));
        assertNull(cache.getBySecondaryKeyIndexTwo("user42"));
    }

    @Test
    void testConcurrencySafety() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        int totalOps = 500;
        CountDownLatch latch = new CountDownLatch(totalOps);

        for (int i = 0; i < totalOps; i++) {
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

        assertTrue(cache.size() <= 10);
    }
}
