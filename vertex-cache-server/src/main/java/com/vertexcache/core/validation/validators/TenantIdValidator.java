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
package com.vertexcache.core.validation.validators;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;

public class TenantIdValidator implements Validator {
    private final String tenantId;
    private static final String RESERVED_DEFAULT = "default";

    public TenantIdValidator(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public void validate() {
        if (tenantId == null || tenantId.isBlank()) {
            throw new VertexCacheValidationException("tenantId is required");
        }

        if (Config.getInstance().getAuthWithTenantConfigLoader().isTenantKeyPrefixingEnabled() &&
                RESERVED_DEFAULT.equalsIgnoreCase(tenantId.trim())) {
            throw new VertexCacheValidationException("'default' is a reserved tenant ID and cannot be used when tenant key prefixing is enabled");
        }

        if (!tenantId.matches("^[a-zA-Z0-9-_]+$")) {
            throw new VertexCacheValidationException("tenantId must be alphanumeric (plus dash/underscore)");
        }
    }
}
