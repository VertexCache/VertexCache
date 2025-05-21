package com.vertexcache.module.metric;

import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.module.metric.service.*;

public class MetricModule extends Module {

    private MetricAccess metricAccess;
    private MetricRegistry metricRegistry;
    private DefaultMetricCollector metricCollector;
    private MetricAnalysisHelper metricAnalysisHelper;
    private HotKeyTracker hotKeyTracker;
    private ClientCommandCounters clientCommandCounters;

    @Override
    protected void onValidate() {
        // No-op for now
    }

    @Override
    protected void onStart() {
        this.metricRegistry = new MetricRegistry();
        this.hotKeyTracker = new HotKeyTracker();
        this.clientCommandCounters = new ClientCommandCounters();
        this.metricCollector = new DefaultMetricCollector(metricRegistry);
        this.metricAnalysisHelper = new MetricAnalysisHelper(metricRegistry, hotKeyTracker, clientCommandCounters);

        metricAccess = new MetricAccess();
        metricAccess.setMetricRegistry(this.metricRegistry);
        metricAccess.setMetricCollector(this.metricCollector);
        metricAccess.setMetricAnalysisHelper(this.metricAnalysisHelper);
        metricAccess.setHotKeyTracker(this.hotKeyTracker);
        metricAccess.setClientCommandCounters(this.clientCommandCounters);

        this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
    }

    @Override
    protected void onStop() {
        this.setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

    public MetricAccess getMetricAccess() {
        return metricAccess;
    }
}
