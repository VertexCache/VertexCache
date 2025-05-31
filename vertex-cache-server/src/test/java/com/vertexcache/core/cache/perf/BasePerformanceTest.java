package com.vertexcache.core.cache.perf;

import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import com.vertexcache.core.cache.model.EvictionPolicy;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BasePerformanceTest {

    protected void runPerformance(String label, int ops) throws VertexCacheTypeException {
        Instant start = Instant.now();

        for (int i = 0; i < ops; i++) {
            Cache.getInstance().put("k" + i, UUID.randomUUID().toString());
        }

        for (int i = 0; i < ops; i++) {
            Cache.getInstance().get("k" + i);
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        double opsPerSec = (ops * 2) / (duration.toMillis() / 1000.0);

        PerformanceResultLogger.record(label, ops, duration, opsPerSec);
        assertTrue(opsPerSec > 5000, "Expected ops/sec above minimum threshold");
    }

    @AfterAll
    static void printFinalResults() {
        PerformanceResultLogger.printMarkdownTable();
    }
}
