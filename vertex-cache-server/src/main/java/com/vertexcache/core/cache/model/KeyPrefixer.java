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
package com.vertexcache.core.cache.model;

import com.vertexcache.core.setting.Config;
import com.vertexcache.server.session.ClientSessionContext;

/**
 * Utility class responsible for applying and stripping key prefixes to support multi-tenant cache isolation.
 *
 * Prefixing ensures that keys from different tenants or namespaces do not collide within the shared cache space.
 * This is typically applied at the boundary of cache operations (e.g., get, set, delete).
 *
 * Example:
 * - Input key: "user:123"
 * - Tenant prefix: "tenantA"
 * - Resulting stored key: "tenantA:user:123"
 *
 * The prefix strategy can be customized based on tenant identifiers, environments, or application domains.
 */
public class KeyPrefixer {

    private static final String SEPARATOR = "::";

    public static String prefixKey(String key, ClientSessionContext context) {
        if (!Config.getInstance().getAuthWithTenantConfigLoader().isAuthEnabled() ||
                !Config.getInstance().getAuthWithTenantConfigLoader().isTenantKeyPrefixingEnabled() ||
                context == null ||
                context.getTenantId() == null) {
            return key;
        }
        return context.getTenantId().getValue() + SEPARATOR + key;
    }

    public static String removePrefix(String fullKey, ClientSessionContext context) {
        if (!Config.getInstance().getAuthWithTenantConfigLoader().isAuthEnabled() ||
                !Config.getInstance().getAuthWithTenantConfigLoader().isTenantKeyPrefixingEnabled() ||
                context == null ||
                context.getTenantId() == null) {
            return fullKey;
        }

        String prefix = context.getTenantId().getValue() + SEPARATOR;
        return fullKey.startsWith(prefix) ? fullKey.substring(prefix.length()) : fullKey;
    }
}
