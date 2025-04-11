package com.vertexcache.module.admin;

import com.vertexcache.core.module.Module;

public class AdminModule extends Module {

    @Override
    protected void onStart() {
        System.out.println("Admin module started");
    }

    @Override
    protected void onStop() {
        System.out.println("Admin module stopped");
    }
}
