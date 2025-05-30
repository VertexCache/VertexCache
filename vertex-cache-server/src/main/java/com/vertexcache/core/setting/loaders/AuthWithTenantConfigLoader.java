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
package com.vertexcache.core.setting.loaders;

import com.vertexcache.common.config.reader.EnvLoader;
import com.vertexcache.core.setting.ConfigKey;
import com.vertexcache.core.setting.model.LoaderBase;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Configuration loader responsible for validating authentication and multi-tenant settings.
 *
 * Handles the following:
 * - Whether authentication is enabled
 * - Whether multi-tenant key prefixing is enforced
 *
 * Multi-tenancy requires authentication to distinguish between tenants securely.
 * If authentication is disabled, multi-tenant mode cannot be enabled, everything will be
 * used under "default" tenant.
 *
 * Ensures that tenant-aware isolation and auth enforcement are configured correctly
 * before enabling secure access to features like REST API or ADMIN commands.
 */
public class AuthWithTenantConfigLoader extends LoaderBase {

    private boolean enableAuth;
    private boolean enableTenantKeyPrefix = ConfigKey.ENABLE_TENANT_KEY_PREFIX_DEFAULT;

    @Override
    public void load() {
        this.enableAuth = false;
        this.enableTenantKeyPrefix = false;
        if (this.getConfigLoader().isExist(ConfigKey.ENABLE_AUTH)) {
            this.enableAuth = Boolean.parseBoolean(this.getConfigLoader().getProperty(ConfigKey.ENABLE_AUTH));
            if (this.enableAuth && this.getConfigLoader().isExist(ConfigKey.ENABLE_TENANT_KEY_PREFIX)) {
                this.enableTenantKeyPrefix = Boolean.parseBoolean(this.getConfigLoader().getProperty(ConfigKey.ENABLE_TENANT_KEY_PREFIX));
            }
        }
    }

    public boolean isAuthEnabled() { return enableAuth; }

    public List<String> getRawAuthClientEntries() {
        if (!(this.getConfigLoader() instanceof EnvLoader env)) return Collections.emptyList();

        return env.getEnvVariables().entrySet().stream()
                .filter(e -> e.getKey().startsWith(ConfigKey.AUTH_CLIENTS_PREFIX))
                .map(Map.Entry::getValue)
                .filter(val -> val != null && !val.isBlank())
                .toList();
    }

    public boolean isTenantKeyPrefixingEnabled() {
        return enableTenantKeyPrefix;
    }
}
