package com.vertexcache.module.metric;

import com.vertexcache.core.module.Module;

public class MetricModule  extends Module {

    @Override
    protected void onStart() {
        System.out.println("Metric module started");
    }

    @Override
    protected void onStop() {
        System.out.println("Metric module stopped");
    }
}
