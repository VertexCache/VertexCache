package com.vertexcache.module.alert;

import com.vertexcache.core.module.Module;

public class AlertModule  extends Module {

    @Override
    protected void onStart() {
        System.out.println("Alert module started");
    }

    @Override
    protected void onStop() {
        System.out.println("Alert module stopped");
    }
}
