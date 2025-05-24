package com.vertexcache.core.cache.impl;

import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class CacheMRUTest {

    private CacheMRU<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new CacheMRU<>(3);
    }

    @Test
    void testPutAndGet() throws VertexCacheTypeException {
        cache.put("a", "apple");
        cache.put("b", "banana");

        assertEquals("apple", cache.get("a"));
        assertEquals("banana", cache.get("b"));
    }

    @Test
    void testMRUEviction() throws VertexCacheTypeException {
        cache.put("x", "X");
        cache.put("y", "Y");
        cache.put("z", "Z");

        // Access 'z' last; should be the most recently used
        cache.get("z");

        // Insert another entry to trigger MRU eviction
        cache.put("w", "W");

        // 'z' should be evicted as it was most recently accessed
        assertNull(cache.get("z"));
        assertNotNull(cache.get("x"));
        assertNotNull(cache.get("y"));
        assertNotNull(cache.get("w"));
    }

    @Test
    void testRemove() throws VertexCacheTypeException {
        cache.put("c", "cookie");
        cache.remove("c");
        assertNull(cache.get("c"));
    }

    @Test
    void testSecondaryIndexTracking() throws VertexCacheTypeException {
        cache.put("u1", "user", "email@test.com", "id-001");

        assertEquals("user", cache.getBySecondaryKeyIndexOne("email@test.com"));
        assertEquals("user", cache.getBySecondaryKeyIndexTwo("id-001"));

        cache.remove("u1");

        assertNull(cache.getBySecondaryKeyIndexOne("email@test.com"));
        assertNull(cache.getBySecondaryKeyIndexTwo("id-001"));
    }

    @Test
    void testConcurrencySafety() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(6);
        CountDownLatch latch = new CountDownLatch(500);

        for (int i = 0; i < 500; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    String key = "k" + (id % 10);
                    cache.put(key, "v" + id, "idx1-" + id, "idx2-" + id);
                    cache.get(key);
                    cache.remove("nonexistent" + id); // no-op test
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

