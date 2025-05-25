package com.vertexcache.module.smart.service;

import com.vertexcache.core.cache.CacheAccessService;
import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.alert.AlertModule;
import com.vertexcache.module.metric.MetricModule;

import java.util.Optional;

abstract public class BaseAlertService {

    private final AlertModule alertModule;
    private final MetricModule metricModule;
    private CacheAccessService cacheAccessService;

    public BaseAlertService() throws VertexCacheException {

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

    abstract public void start();
    abstract public void shutdown();

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
