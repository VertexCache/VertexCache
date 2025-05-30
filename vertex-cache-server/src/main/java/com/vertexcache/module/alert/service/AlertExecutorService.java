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
package com.vertexcache.module.alert.service;

import com.vertexcache.module.alert.model.AlertEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * AlertExecutorService is responsible for processing and dispatching alert events
 * within the VertexCache system. It receives AlertEvent instances and executes
 * the corresponding logic, such as invoking webhook handlers or logging alerts.
 *
 * This service acts as the core execution layer for the alerting pipeline,
 * ensuring that alerts are handled asynchronously, reliably, and according to
 * system configuration.
 */
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
