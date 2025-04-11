package com.vertexcache.module.cluster;

import com.vertexcache.core.module.Module;

public class ClusterModule  extends Module {

    @Override
    protected void onStart() {
        System.out.println("Cluster module started");
    }

    @Override
    protected void onStop() {
        System.out.println("Cluster module stopped");
    }
}
