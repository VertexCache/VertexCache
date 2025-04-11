package com.vertexcache.module.exporter;

import com.vertexcache.common.util.Lazy;
import com.vertexcache.core.module.Module;

public class MetricExporterModule  extends Module {

    private Lazy<PrometheusExporter> prometheusExporter = new Lazy<>(PrometheusExporter::new);

    @Override
    protected void onStart() {
        System.out.println("Metric Exporter module started");
    }

    @Override
    protected void onStop() {
        System.out.println("Metric Exporter module stopped");
    }

    public PrometheusExporter getPrometheusExporter() {
        return prometheusExporter.get();
    }
}
