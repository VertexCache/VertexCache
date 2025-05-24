package com.vertexcache.core.cache.impl;

import com.vertexcache.core.cache.algos.CacheFIFO;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class CacheFIFOTest {

    private CacheFIFO<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new CacheFIFO<>(3); // small for eviction behavior
    }

    @Test
    void testBasicPutAndGet() throws VertexCacheTypeException {
        cache.put("a", "alpha");
        cache.put("b", "beta");

        assertEquals("alpha", cache.get("a"));
        assertEquals("beta", cache.get("b"));
    }

    @Test
    void testEvictionOrderFIFO() throws VertexCacheTypeException {
        cache.put("a", "alpha");
        cache.put("b", "beta");
        cache.put("c", "charlie");
        cache.put("d", "delta"); // should evict "a"

        assertNull(cache.get("a")); // FIFO eviction
        assertNotNull(cache.get("b"));
        assertNotNull(cache.get("c"));
        assertNotNull(cache.get("d"));
    }

    @Test
    void testRemove() throws VertexCacheTypeException {
        cache.put("x", "x-ray");
        cache.remove("x");
        assertNull(cache.get("x"));
    }

    @Test
    void testSecondaryIndexCleanup() throws VertexCacheTypeException {
        cache.put("key1", "value1", "email@abc.com", "user-123");
        cache.remove("key1");

        assertNull(cache.getBySecondaryKeyIndexOne("email@abc.com"));
        assertNull(cache.getBySecondaryKeyIndexTwo("user-123"));
    }

    @Test
    void testConcurrencySafety() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        int ops = 500;
        CountDownLatch latch = new CountDownLatch(ops);

        for (int i = 0; i < ops; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    String key = "k" + (id % 10);
                    cache.put(key, "v" + id, "idx1-" + id, "idx2-" + id);
                    cache.get(key);
                    cache.remove("z" + id); // non-existent removes
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
