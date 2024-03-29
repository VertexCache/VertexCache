package com.vertexcache.common.console;

import java.util.concurrent.*;

public class ConsoleOutput {
    private static final int QUEUE_CAPACITY = 1000;
    private static final int BATCH_SIZE = 100;
    private static final BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final ExecutorService executor = Executors.newFixedThreadPool(10); // Adjust the pool size as needed

    static {
        for (int i = 0; i < 10; i++) { // Start multiple worker threads
            executor.submit(() -> {
                try {
                    while (true) {
                        processLogBatch();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    private static void processLogBatch() throws InterruptedException {
        StringBuilder batch = new StringBuilder();
        for (int i = 0; i < BATCH_SIZE; i++) {
            String message = queue.take(); // Blocks until a message is available
            batch.append(message).append(System.lineSeparator());
        }
        System.out.print(batch); // Output batch of log messages to console
    }

    public static void println(String message) {
        queue.offer(message); // Non-blocking add to the queue
    }

    public static void shutdown() {
        executor.shutdown();
    }
}

