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
package com.vertexcache.module.auth.service;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.ValidationBatch;
import com.vertexcache.core.validation.validators.ClientIdValidator;
import com.vertexcache.core.validation.validators.RoleValidator;
import com.vertexcache.core.validation.validators.TenantIdValidator;
import com.vertexcache.core.validation.validators.UUIDValidator;
import com.vertexcache.module.auth.model.Role;
import com.vertexcache.module.auth.datastore.AuthStore;
import com.vertexcache.module.auth.exception.VertexCacheAuthModuleException;
import com.vertexcache.module.auth.model.AuthEntry;
import com.vertexcache.module.auth.model.TenantId;

import java.util.List;

/**
 * AuthInitializer is responsible for bootstrapping the authentication system in VertexCache.
 * It loads authentication configuration, initializes the AuthStore, and prepares any
 * supporting components required for secure client validation at runtime.
 *
 * This class is invoked during system startup to ensure that all authentication-related
 * resources are properly configured and ready before accepting client requests.
 */
public class AuthInitializer {

    public static AuthService initializeFromEnv() {
        List<String> rawEntries = Config.getInstance().getAuthWithTenantConfigLoader().getRawAuthClientEntries();
        AuthStore store = new AuthStore();

        for (String line : rawEntries) {
            String[] parts = line.split(":");
            if (parts.length != 4) {
                throw new VertexCacheAuthModuleException("Invalid auth_client entry (must have 4 parts): " + line);
            }

            String clientId = parts[0].trim();
            String tenantId = parts[1].trim();
            String roleRaw = parts[2].trim();
            String token = parts[3].trim();

            ValidationBatch batch = new ValidationBatch();
            batch.check("clientId", new ClientIdValidator(clientId));
            batch.check("tenantId", new TenantIdValidator(tenantId));
            batch.check("role", new RoleValidator(roleRaw));
            batch.check("token", new UUIDValidator(token));

            if (batch.hasErrors()) {
                throw new VertexCacheAuthModuleException("Invalid auth_client [" + line + "]: " + batch.getSummary());
            }

            try {
                store.put(new AuthEntry(
                        clientId,
                        TenantId.fromString(tenantId),
                        Role.fromString(roleRaw),
                        token
                ));
            } catch (Exception e) {
                throw new VertexCacheAuthModuleException("Failed to register auth_client: " + line);
            }
        }

        if (store.list().isEmpty()) {
            throw new VertexCacheAuthModuleException("No valid auth clients found in .env (auth_client_*)");
        }

        AuthService.initialize(store);
        return AuthService.getInstance();
    }
}
