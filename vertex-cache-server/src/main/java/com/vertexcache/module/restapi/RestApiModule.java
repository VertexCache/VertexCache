/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.module.restapi;

import com.vertexcache.common.log.LogHelper;
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
        if(Config.getInstance().getClusterConfigLoader().isPrimaryNode()) {
            this.startService();
        } else {
            this.setModuleStatus(ModuleStatus.STARTUP_STANDBY, "REST API Server will start on Node Promotion");
        }
    }

    public void startService() {
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
