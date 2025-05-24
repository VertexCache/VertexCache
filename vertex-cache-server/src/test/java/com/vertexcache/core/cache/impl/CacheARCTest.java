package com.vertexcache.core.cache.impl;


import com.vertexcache.core.cache.algos.CacheARC;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class CacheARCTest {

    private CacheARC<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new CacheARC<>(3); // small capacity for eviction test
    }

    @Test
    void testBasicPutAndGet() throws VertexCacheTypeException {
        cache.put("a", "apple");
        cache.put("b", "banana");

        assertEquals("apple", cache.get("a"));
        assertEquals("banana", cache.get("b"));
    }

    @Test
    void testEvictionWhenCapacityExceeded() throws VertexCacheTypeException {
        cache.put("a", "apple");
        cache.put("b", "banana");
        cache.put("c", "cherry");
        cache.put("d", "date"); // should evict one of a/b/c

        int hits = 0;
        if (cache.get("a") != null) hits++;
        if (cache.get("b") != null) hits++;
        if (cache.get("c") != null) hits++;
        if (cache.get("d") != null) hits++;

        assertEquals(3, hits, "Only 3 entries should remain due to ARC eviction");
    }

    @Test
    void testRemove() throws VertexCacheTypeException {
        cache.put("x", "x-ray");
        cache.put("y", "yellow");

        cache.remove("x");
        assertNull(cache.get("x"));
        assertEquals("yellow", cache.get("y"));
    }

    @Test
    void testSecondaryKeyAndReverseIndexCleanup() throws VertexCacheTypeException {
        cache.put("k1", "v1", "email@example.com", "user1");
        cache.remove("k1");

        assertNull(cache.getBySecondaryKeyIndexOne("email@example.com"));
        assertNull(cache.getBySecondaryKeyIndexTwo("user1"));
    }

    @Test
    void testConcurrencySafety() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int totalOps = 1000;
        CountDownLatch latch = new CountDownLatch(totalOps);

        for (int i = 0; i < totalOps; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    String key = "key" + (id % 20);
                    String value = "val" + id;
                    cache.put(key, value, "idx1-" + id, "idx2-" + id);
                    cache.get(key);
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(cache.size() <= 20);
    }
}
