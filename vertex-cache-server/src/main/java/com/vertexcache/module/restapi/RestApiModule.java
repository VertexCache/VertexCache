package com.vertexcache.module.restapi;

import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.core.validation.validators.PortValidator;
import com.vertexcache.core.validation.validators.restapi.RestApiTlsValidator;
import com.vertexcache.core.validation.validators.restapi.TokenHeaderValidator;

public class RestApiModule  extends Module {

    @Override
    protected void onValidate() {
        var config = Config.getInstance().getRestApiConfigLoader();

        if (!config.isEnableRestApi()) {
            this.setModuleStatus(ModuleStatus.DISABLED, "REST API disabled via config");
            return;
        }

        try {
            new PortValidator(config.getPort(), "REST API port").validate();
        } catch (VertexCacheValidationException ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, ex.getMessage());
            return;
        }

        try {
            new TokenHeaderValidator(config.getTokenHeader()).validate();
        } catch (VertexCacheValidationException ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, ex.getMessage());
            return;
        }

        try {
            new RestApiTlsValidator().validate();
        } catch (VertexCacheValidationException ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, ex.getMessage());
            return;
        }
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
