package com.vertexcache.module.rest;

import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;

public class RestApiModule  extends Module {

    @Override
    protected void onStart() {
        this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
    }

    @Override
    protected void onStop() {
        this.setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }
}
