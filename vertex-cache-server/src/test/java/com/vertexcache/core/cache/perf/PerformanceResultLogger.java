package com.vertexcache.core.cache.perf;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PerformanceResultLogger {

    private static final List<Result> results = new ArrayList<>();

    public static void record(String algorithm, int ops, Duration duration, double opsPerSec) {
        results.add(new Result(algorithm, ops * 2, duration.toMillis(), opsPerSec));
    }

    public static void printMarkdownTable() {
        System.out.println();
        System.out.println("| Algorithm | Total Ops | Duration (ms) | Ops/sec |");
        System.out.println("|-----------|------------|----------------|---------|");
        for (Result r : results) {
            System.out.printf("| %-9s | %,10d | %,14d | %,7.0f |\n",
                    r.algorithm, r.totalOps, r.durationMs, r.opsPerSec);
        }
    }

    private static class Result {
        String algorithm;
        int totalOps;
        long durationMs;
        double opsPerSec;

        Result(String algorithm, int totalOps, long durationMs, double opsPerSec) {
            this.algorithm = algorithm;
            this.totalOps = totalOps;
            this.durationMs = durationMs;
            this.opsPerSec = opsPerSec;
        }
    }
}

