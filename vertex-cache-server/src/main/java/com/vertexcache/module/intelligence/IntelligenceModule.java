package com.vertexcache.module.intelligence;

import com.vertexcache.core.module.Module;

public class IntelligenceModule  extends Module {

    @Override
    protected void onStart() {
        System.out.println("Intelligence module started");
    }

    @Override
    protected void onStop() {
        System.out.println("Intelligence module stopped");
    }
}
