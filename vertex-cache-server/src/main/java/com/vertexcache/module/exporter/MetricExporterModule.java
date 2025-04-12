package com.vertexcache.module.exporter;

import com.vertexcache.common.util.Lazy;
import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;

public class MetricExporterModule  extends Module {

    private Lazy<PrometheusExporter> prometheusExporter = new Lazy<>(PrometheusExporter::new);

    @Override
    protected void onStart() {
        this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
    }

    @Override
    protected void onStop() {
        this.setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

    @Override
    protected void onError() {
        this.setModuleStatus(ModuleStatus.ERROR_RUNTIME);
    }

    public PrometheusExporter getPrometheusExporter() {
        return prometheusExporter.get();
    }
}
