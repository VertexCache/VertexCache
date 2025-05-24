package com.vertexcache.core.cache.impl;

import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class CacheTinyLFUTest {

    private CacheTinyLFU<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new CacheTinyLFU<>(3); // Small to trigger eviction
    }

    @Test
    void testPutAndGet() throws VertexCacheTypeException {
        cache.put("a", "alpha");
        cache.put("b", "beta");

        assertEquals("alpha", cache.get("a"));
        assertEquals("beta", cache.get("b"));
    }

    @Test
    void testLFUEvictionBehavior() throws VertexCacheTypeException {
        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");

        // Access 'a' more often to make it less likely to be evicted
        cache.get("a");
        cache.get("a");
        cache.get("b");

        // Insert new key to trigger eviction
        cache.put("d", "D");

        int hits = 0;
        for (String key : new String[]{"a", "b", "c", "d"}) {
            if (cache.get(key) != null) hits++;
        }

        assertEquals(3, hits, "TinyLFU should evict one of the less frequently used keys");
    }

    @Test
    void testRemove() throws VertexCacheTypeException {
        cache.put("x", "X");
        cache.remove("x");
        assertNull(cache.get("x"));
    }

    @Test
    void testSecondaryIndexCleanup() throws VertexCacheTypeException {
        cache.put("user", "user", "user@example.com", "user-77");

        assertEquals("user", cache.getBySecondaryKeyIndexOne("user@example.com"));
        assertEquals("user", cache.getBySecondaryKeyIndexTwo("user-77"));

        cache.remove("user");

        assertNull(cache.getBySecondaryKeyIndexOne("user@example.com"));
        assertNull(cache.getBySecondaryKeyIndexTwo("uid-77"));
    }

    @Test
    void testConcurrencySafety() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(6);
        CountDownLatch latch = new CountDownLatch(400);

        for (int i = 0; i < 400; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    String key = "k" + (id % 10);
                    cache.put(key, "v" + id, "idx1-" + id, "idx2-" + id);
                    cache.get(key);
                    cache.remove("junk" + id); // test safe remove
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

