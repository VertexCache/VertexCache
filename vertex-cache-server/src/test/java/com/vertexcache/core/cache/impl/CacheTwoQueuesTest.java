package com.vertexcache.core.cache.impl;

import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class CacheTwoQueuesTest {

    private CacheTwoQueues<String, String> cache;

    @BeforeEach
    void setup() {
        cache = new CacheTwoQueues<>(5);
    }

    @Test
    void testPutAndGet() throws VertexCacheTypeException {
        cache.put("a", "apple");
        cache.put("b", "banana");

        assertEquals("apple", cache.get("a"));
        assertEquals("banana", cache.get("b"));
    }

    @Test
    void testEvictionBehavior() throws VertexCacheTypeException {
        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");
        cache.get("a"); // promote to Am
        cache.put("d", "D");
        cache.put("e", "E");
        cache.put("f", "F"); // trigger eviction

        int retained = 0;
        for (String key : new String[]{"a", "b", "c", "d", "e", "f"}) {
            if (cache.get(key) != null) retained++;
        }

        assertTrue(retained <= 5, "At most 5 entries should remain in the cache");
    }

    @Test
    void testRemove() throws VertexCacheTypeException {
        cache.put("x", "xray");
        cache.remove("x");

        assertNull(cache.get("x"));
    }

    @Test
    void testSecondaryIndexSupport() throws VertexCacheTypeException {
        cache.put("u", "user", "email@twoq.com", "id-007");

        assertEquals("user", cache.getBySecondaryKeyIndexOne("email@twoq.com"));
        assertEquals("user", cache.getBySecondaryKeyIndexTwo("id-007"));

        cache.remove("u");

        assertNull(cache.getBySecondaryKeyIndexOne("email@twoq.com"));
        assertNull(cache.getBySecondaryKeyIndexTwo("id-007"));
    }

    @Test
    void testConcurrencySafety() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(300);

        for (int i = 0; i < 300; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    String key = "k" + (id % 10);
                    cache.put(key, "v" + id, "idx1-" + id, "idx2-" + id);
                    cache.get(key);
                    cache.remove("junk-" + id); // should be safe
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertTrue(cache.size() <= 10);
    }
}
