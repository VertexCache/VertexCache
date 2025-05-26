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
package com.vertexcache.module.auth.util;

import com.vertexcache.core.module.ModuleName;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.module.auth.AuthModule;
import com.vertexcache.module.auth.service.AuthService;

import java.util.Optional;

public class AuthModuleHelper {

    private AuthModuleHelper() {
        // Prevent instantiation
    }

    /**
     * Safely retrieves the AuthModule from the ModuleRegistry, if loaded.
     */
    public static Optional<AuthModule> getInstance() {
        return ModuleRegistry.getInstance()
                .getModuleByEnum(ModuleName.AUTH)
                .filter(m -> m instanceof AuthModule)
                .map(m -> (AuthModule) m);
    }

    /**
     * Safely retrieves the AuthService from the AuthModule, if available.
     */
    public static Optional<AuthService> getAuthService() {
        return getInstance().map(AuthModule::getAuthService);
    }
}
