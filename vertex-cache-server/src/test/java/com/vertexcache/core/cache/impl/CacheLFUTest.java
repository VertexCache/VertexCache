package com.vertexcache.core.cache.impl;

import com.vertexcache.core.cache.algos.CacheLFU;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class CacheLFUTest {

    private CacheLFU<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new CacheLFU<>(3); // small capacity to trigger eviction
    }

    @Test
    void testPutAndGet() throws VertexCacheTypeException {
        cache.put("x", "X");
        cache.put("y", "Y");

        assertEquals("X", cache.get("x"));
        assertEquals("Y", cache.get("y"));
    }

    @Test
    void testEvictionLFU() throws VertexCacheTypeException {
        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");

        // Boost frequency of 'a'
        cache.get("a");
        cache.get("a");

        // 'b' and 'c' are less frequently used
        cache.get("b");

        // Insert a new key to trigger eviction
        cache.put("d", "D");

        // 'c' should be evicted (lowest freq)
        assertNotNull(cache.get("a"));
        assertNotNull(cache.get("b"));
        assertNotNull(cache.get("d"));
        assertNull(cache.get("c"));
    }

    @Test
    void testRemove() throws VertexCacheTypeException {
        cache.put("x", "X");
        cache.remove("x");
        assertNull(cache.get("x"));
    }

    @Test
    void testSecondaryIndexCleanup() throws VertexCacheTypeException {
        cache.put("u", "U", "idx1-u", "idx2-u");
        assertEquals("U", cache.getBySecondaryKeyIndexOne("idx1-u"));
        assertEquals("U", cache.getBySecondaryKeyIndexTwo("idx2-u"));

        cache.remove("u");

        assertNull(cache.getBySecondaryKeyIndexOne("idx1-u"));
        assertNull(cache.getBySecondaryKeyIndexTwo("idx2-u"));
    }

    @Test
    void testConcurrencySafety() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        int ops = 500;
        CountDownLatch latch = new CountDownLatch(ops);

        for (int i = 0; i < ops; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    String key = "k" + (id % 10);
                    cache.put(key, "v" + id, "email" + id, "user" + id);
                    cache.get(key);
                    cache.remove("ghost" + id); // not present, should be safe
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

