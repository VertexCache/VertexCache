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

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

abstract public class BaseThroughputLoad implements ThroughputLoad {

    private AtomicLong successCount;
    private AtomicLong failureCount;


    private String title;
    private VertexCacheSDK sdk;
    private int threads;
    private int duration;


    public BaseThroughputLoad(String title, VertexCacheSDK sdk, int threads, int duration) {
        this.title = title;
        this.sdk = sdk;
        this.threads = threads;
        this.duration = duration;
        this.init();
    }

    public void execute() throws InterruptedException {
        System.out.printf("Starting VertexBench " + title + " Load Test: %d threads for %d seconds\n", getThreads(), getDuration());

        ExecutorService pool = Executors.newFixedThreadPool(getThreads());

        long endTime = System.currentTimeMillis() + (getDuration() * 1000);

        Runnable task = () -> {
            Random rand = new Random();
            while (System.currentTimeMillis() < endTime) {
                try {
                    this.performOperation(this.getSdk(),rand);
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

    protected abstract void performOperation(VertexCacheSDK sdk, Random rand) throws Exception;

    private void init() {
        sdk.openConnection();
        successCount = new AtomicLong();
        failureCount = new AtomicLong();
    }

    private void displayResults() {
        long totalOps = getSuccessCount().get() + getFailureCount().get();
        double qps = totalOps / (double) getDuration();

        System.out.printf("\n--- VertexBench GET-Only Results ---\n");
        System.out.printf("Total Operations: %d\n", totalOps);
        System.out.printf("Successful Ops : %d\n", getSuccessCount().get());
        System.out.printf("Failed Ops     : %d\n", getFailureCount().get());
        System.out.printf("Average QPS    : %.2f\n", qps);
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
}
