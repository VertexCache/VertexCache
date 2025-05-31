package com.vertexcache.core.cache.perf;

import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import com.vertexcache.core.cache.model.EvictionPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("manual")
public class CacheLFUPerformanceTest extends BasePerformanceTest {

    private final static String PREFIX_LABEL = "LFU";
    private final static EvictionPolicy EVICTION_POLICY = EvictionPolicy.LFU;

    @BeforeEach
    void setUp() {
        int capacity = 100_000;
        Cache.getInstance(EVICTION_POLICY, capacity);
    }

    @AfterEach
    void coolDown() throws InterruptedException {
        System.gc();
        Thread.sleep(100);
    }

    @Test
    void testLowEviction() throws VertexCacheTypeException, InterruptedException {
        int ops = 50_000;
        Cache.getInstance().clear();
        runPerformance(PREFIX_LABEL + "-low", ops);
        System.gc();
        Thread.sleep(100);
    }

    @Test
    void testMediumEviction() throws VertexCacheTypeException, InterruptedException {
        int ops = 100_000;
        Cache.getInstance().clear();
        runPerformance(PREFIX_LABEL + "-mid", ops);
        System.gc();
        Thread.sleep(100);
    }

    @Test
    void testHighEviction() throws VertexCacheTypeException, InterruptedException {
        int ops = 200_000;
        Cache.getInstance().clear();
        runPerformance(PREFIX_LABEL + "-high", ops);
        System.gc();
        Thread.sleep(100);
    }

    @Test
    void testStress() throws VertexCacheTypeException, InterruptedException {
        int capacity = 500_000;
        int ops = 500_000;
        Cache.destroy();
        Cache.getInstance(EVICTION_POLICY, capacity);
        Cache.getInstance().clear();
        runPerformance(PREFIX_LABEL + "-stress", ops);
        System.gc();
        Thread.sleep(100);
    }
}
