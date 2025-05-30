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
package com.vertexcache.module.auth;

import com.vertexcache.core.module.model.Module;
import com.vertexcache.core.module.model.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.auth.exception.VertexCacheAuthModuleException;
import com.vertexcache.module.auth.service.AuthInitializer;
import com.vertexcache.module.auth.service.AuthService;

/**
 * AuthModule initializes and manages the authentication infrastructure in VertexCache.
 * It wires together the AuthService, AuthStore, and related components based on
 * system configuration, enabling secure access control across all client interactions.
 *
 * This module is responsible for loading authentication settings, validating
 * credentials at runtime, and enforcing role-based permissions system-wide.
 */
public class AuthModule extends Module {

    private AuthService authService;

    @Override
    protected void onValidate() {

    }

    @Override
    protected void onStart() {
        try {
            if (Config.getInstance().getAuthWithTenantConfigLoader().getRawAuthClientEntries().isEmpty()) {
                throw new VertexCacheAuthModuleException("Require at least one client defined when auth is enabled.");
            }

            this.authService = AuthInitializer.initializeFromEnv();

            reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Auth clients loaded");

        } catch (VertexCacheAuthModuleException e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        this.authService = null;
        setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

    public AuthService getAuthService() {
        return authService;
    }
}
