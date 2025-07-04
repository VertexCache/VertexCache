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
package com.vertexcache.module.smart.service;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.cache.CacheAccessService;
import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.alert.AlertModule;
import com.vertexcache.module.metric.MetricModule;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Abstract base class for alert services within VertexCache.
 *
 * Manages periodic scheduling of alert evaluations using a shared executor.
 * Provides hooks for starting, stopping, and performing alert logic via the
 * abstract {@code evaluate()} method, which subclasses must implement.
 *
 * Integrates with AlertModule and MetricModule if they are enabled and available,
 * and provides access to CacheAccessService for cache-related queries.
 */
abstract public class BaseAlertService {

    private ScheduledFuture<?> scheduledFuture;
    private ScheduledExecutorService scheduler;

    private final String name;
    private final long intervalSeconds;

    private final AlertModule alertModule;
    private final MetricModule metricModule;
    private CacheAccessService cacheAccessService;

    public BaseAlertService(String name, long intervalSeconds) throws VertexCacheException {

        this.name = name;
        this.intervalSeconds = intervalSeconds;
        this.cacheAccessService = new CacheAccessService();

        Optional<AlertModule> optAlertModule = ModuleRegistry.getInstance().getModule(AlertModule.class);
        if (!optAlertModule.isPresent()) {
            if (Config.getInstance().getAlertConfigLoader().isEnableAlerting()) {
                throw new VertexCacheException("AlertModule not enabled");
            }
            this.alertModule = null;
        } else {
            this.alertModule = optAlertModule.get();
        }

        Optional<MetricModule> optMetricModule = ModuleRegistry.getInstance().getModule(MetricModule.class);
        if (!optMetricModule.isPresent()) {
            if (Config.getInstance().getMetricConfigLoader().isEnableMetric()) {
                throw new VertexCacheException("MetricModule not enabled");
            }
            this.metricModule = null; // or skip assignment entirely
        } else {
            this.metricModule = optMetricModule.get();
        }

    }

    /**
     * Starts periodic evaluation if intervalSeconds > 0.
     */
    public void start(ScheduledExecutorService sharedScheduler) {
        this.scheduler = sharedScheduler;


        if (intervalSeconds > 0) {
            scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
                try {
                    evaluate();
                } catch (Exception e) {
                    LogHelper.getInstance().logFatal("[SmartModule] AlertService '" + name + "' failed: " + e.getMessage());
                }
            }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * Stops the scheduled task (if any).
     */
    public void stop() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }
        onStop(); // Hook for additional cleanup
    }

    /**
     * Called periodically if intervalSeconds > 0.
     */
    protected abstract void evaluate() throws VertexCacheTypeException;

    /**
     * Optional hook for custom cleanup logic.
     */
    protected void onStop() {
        // default no-op
    }

    public String getName() {
        return name;
    }

    public CacheAccessService getCacheAccessService() {
        return cacheAccessService;
    }

    public AlertModule getAlertModule() {
        return alertModule;
    }

    public MetricModule getMetricModule() {
        return metricModule;
    }
}
