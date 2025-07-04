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
package com.vertexcache.module.metric;

import com.vertexcache.core.module.model.Module;
import com.vertexcache.core.module.model.ModuleStatus;
import com.vertexcache.core.util.runtime.RuntimeInfo;
import com.vertexcache.module.metric.analysis.HotKeyTracker;
import com.vertexcache.module.metric.analysis.MetricAnalysisHelper;
import com.vertexcache.module.metric.core.collectors.DefaultMetricCollector;
import com.vertexcache.module.metric.core.MetricRegistry;
import com.vertexcache.module.metric.counter.ClientCommandCounters;
import com.vertexcache.module.metric.service.*;

/**
 * Initializes and manages all metric-related components in VertexCache.
 *
 * Sets up the metric registry, collectors, analysis helpers, and hot key tracking.
 * Exposes these via MetricAccess for other components to record and retrieve metrics.
 *
 * onStart() handles setup of all components and sets startup status.
 * onStop() updates the module status to indicate a successful shutdown.
 */
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

        // Trigger off the start time
        long uptime = RuntimeInfo.getUptimeMillis();

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
