package com.vertexcache.module.restapi;

import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.core.validation.validators.PortValidator;
import com.vertexcache.core.validation.validators.restapi.RestApiTlsValidator;
import com.vertexcache.core.validation.validators.restapi.TokenHeaderValidator;
import com.vertexcache.module.restapi.server.RestApiServer;

public class RestApiModule extends Module {

    private RestApiServer server;

    @Override
    protected void onValidate() {
        var config = Config.getInstance().getRestApiConfigLoader();

        try {
            if(config.isRequireAuth() && !Config.getInstance().getAuthWithTenantConfigLoader().isAuthEnabled()) {
                throw new VertexCacheValidationException("RestApiAuth 'rest_api_require_auth' set enabled requires 'enable_auth' to be enabled");
            }
            new PortValidator(config.getPort(), "REST API port").validate();
            new TokenHeaderValidator(config.getTokenHeader()).validate();
            new RestApiTlsValidator().validate();
        } catch (VertexCacheValidationException ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, ex.getMessage());
        }
    }

    @Override
    protected void onStart() {
        var config = Config.getInstance().getRestApiConfigLoader();

        if (!config.isEnableRestApi()) {
            this.setModuleStatus(ModuleStatus.DISABLED, "REST API is disabled via config");
            return;
        }

        if (config.isRequireTls()) {
            if (Config.getInstance().getSecurityConfigLoader().getTlsCertificate() == null || Config.getInstance().getSecurityConfigLoader().getTlsCertificate().isBlank()) {
                this.setModuleStatus(ModuleStatus.STARTUP_FAILED, "TLS is required for REST API, but no certificate is set.");
                return;
            }

            if (Config.getInstance().getSecurityConfigLoader().getTlsPrivateKey() == null || Config.getInstance().getSecurityConfigLoader().getTlsPrivateKey().isBlank()) {
                this.setModuleStatus(ModuleStatus.STARTUP_FAILED, "TLS is required for REST API, but no private key is set.");
                return;
            }
        }

        try {
            server = new RestApiServer();
            server.start();
            this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL, "REST API started on port " + (config.isRequireTls() ? config.getPortTls() : config.getPort()));
        } catch (Exception ex) {
            this.setModuleStatus(ModuleStatus.STARTUP_FAILED, "Failed to start REST API: " + ex.getMessage());
        }
    }

    @Override
    protected void onStop() {
        if (server != null) {
            server.stop();
        }
        this.setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }
}
