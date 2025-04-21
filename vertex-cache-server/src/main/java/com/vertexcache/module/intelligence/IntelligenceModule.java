package com.vertexcache.module.intelligence;

import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;

public class IntelligenceModule  extends Module {

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
