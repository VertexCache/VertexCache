package com.vertexcache.module.alert.service;

import com.vertexcache.module.alert.model.AlertEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class AlertExecutorService {
    private static final Logger LOG = Logger.getLogger(AlertExecutorService.class.getSimpleName());
    private final AlertWebhookDispatcher dispatcher;
    private final ExecutorService executor;

    public AlertExecutorService(AlertWebhookDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "AlertDispatcherThread");
            t.setDaemon(true);
            return t;
        });
    }

    public void dispatchAsync(AlertEvent event) {
        executor.submit(() -> {
            try {
                dispatcher.dispatch(event);
            } catch (Exception e) {
                LOG.severe("[AlertExecutorService] Dispatch failed for event " + event.getEvent() + ": " + e.getMessage());
            }
        });
    }

    public void shutdown() {
        LOG.info("[AlertExecutorService] Shutting down alert executor.");
        executor.shutdown(); // Or use shutdownNow() if you want immediate shutdown
    }
}
