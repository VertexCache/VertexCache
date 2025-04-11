package com.vertexcache.module.rest;

import com.vertexcache.core.module.Module;

public class RestApiModule  extends Module {

    @Override
    protected void onStart() {
        System.out.println("Rest API module started");
    }

    @Override
    protected void onStop() {
        System.out.println("Rest API module stopped");
    }
}
