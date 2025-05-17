package com.vertexcache.module.exporter;

import com.vertexcache.common.util.Lazy;
import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;

public class ExporterModule extends Module {


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
