/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.vertexbench.load;

import com.vertexcache.sdk.VertexCacheSDK;
import com.vertexcache.vertexbench.util.VertexBenchConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

abstract public class BaseThroughputLoad implements ThroughputLoad {

    private AtomicLong successCount;
    private AtomicLong failureCount;

    private LoadType loadType;

    private VertexBenchConfig vertexBenchConfig;

    private VertexCacheSDK sdk;
    private int threads;
    private int duration;



    private boolean enableLatencyTracking = true;
    private List<Long> latencies = Collections.synchronizedList(new ArrayList<>());

    public BaseThroughputLoad(LoadType loadType, VertexBenchConfig vertexBenchConfig) {
        this.loadType = loadType;
        this.vertexBenchConfig = vertexBenchConfig;
        this.sdk = vertexBenchConfig.getVertexCacheSDK();
        this.threads = vertexBenchConfig.getThreads();
        this.duration = vertexBenchConfig.getDuration();
        this.init();
    }

    public void execute() throws InterruptedException {
        System.out.printf("Starting VertexBench %s Load Test: %d threads for %d seconds\n", loadType.getTitle(), getThreads(), getDuration());

        successCount.set(0);
        failureCount.set(0);
        latencies.clear();

        ExecutorService pool = Executors.newFixedThreadPool(getThreads());
        long endTime = System.currentTimeMillis() + (getDuration() * 1000);

        Runnable task = () -> {
            Random rand = new Random();
            while (System.currentTimeMillis() < endTime) {
                try {
                    long startTime = enableLatencyTracking ? System.nanoTime() : 0;

                    this.performOperation(rand);

                    if (enableLatencyTracking) {
                        long duration = System.nanoTime() - startTime;
                        latencies.add(duration);
                    }

                    getSuccessCount().incrementAndGet();
                } catch (Exception e) {
                    getFailureCount().incrementAndGet();
                }
            }
        };

        for (int i = 0; i < getThreads(); i++) {
            pool.submit(task);
        }

        pool.shutdown();
        pool.awaitTermination(getDuration() + 5, TimeUnit.SECONDS);

        this.displayResults();
    }

    protected abstract void performOperation(Random rand);

    private void init() {
        successCount = new AtomicLong();
        failureCount = new AtomicLong();
    }

    private void displayResults() {
        long totalOps = getSuccessCount().get() + getFailureCount().get();
        double qps = totalOps / (double) getDuration();

        System.out.printf("\n--- VertexBench %s Results ---\n", loadType.getTitle());
        System.out.printf("Total Operations: %d\n", totalOps);
        System.out.printf("Successful Ops : %d\n", getSuccessCount().get());
        System.out.printf("Failed Ops     : %d\n", getFailureCount().get());
        System.out.printf("Average QPS    : %.2f\n", qps);

        if (enableLatencyTracking && !latencies.isEmpty()) {
            List<Long> sortedLatencies = new ArrayList<>(latencies);
            Collections.sort(sortedLatencies);

            long p50 = sortedLatencies.get(sortedLatencies.size() * 50 / 100);
            long p95 = sortedLatencies.get(sortedLatencies.size() * 95 / 100);
            long p99 = sortedLatencies.get(sortedLatencies.size() * 99 / 100);

            System.out.printf("Latency (nanoseconds): P50=%d, P95=%d, P99=%d\n", p50, p95, p99);
        }
    }

    public VertexBenchConfig getVertexBenchConfig() {
        return vertexBenchConfig;
    }

    public VertexCacheSDK getSdk() {
        return this.sdk;
    }

    public int getThreads() {
        return this.threads;
    }

    public int getDuration() {
        return this.duration;
    }

    public AtomicLong getSuccessCount() {
        return successCount;
    }

    public AtomicLong getFailureCount() {
        return failureCount;
    }

    public boolean isEnableLatencyTracking() {
        return enableLatencyTracking;
    }

    public void setEnableLatencyTracking(boolean enableLatencyTracking) { this.enableLatencyTracking = enableLatencyTracking;}
}
