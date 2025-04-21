package com.vertexcache.module.admin;

import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;

public class AdminModule extends Module {

    @Override
    protected void onInitialize() {

    }

    @Override
    protected void onValidate() {

    }

    @Override
    protected void onStart() {
        this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
    }

    @Override
    protected void onStop() {
        this.setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

}
