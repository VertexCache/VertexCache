package com.vertexcache.core.validation.validators;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.ValidatorHandler;
import com.vertexcache.core.validation.VertexCacheValidationException;

public class TenantIdValidator implements ValidatorHandler<String> {

    private static final String RESERVED_DEFAULT = "default";

    @Override
    public void validate(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new VertexCacheValidationException("tenantId is required");
        }

        // Prevent use of 'default' if tenant prefixing is enabled
        if (Config.getInstance().isTenantKeyPrefixingEnabled() &&
                RESERVED_DEFAULT.equalsIgnoreCase(tenantId.trim())) {
            throw new VertexCacheValidationException("'default' is a reserved tenant ID and cannot be used when tenant key prefixing is enabled");
        }

        if (!tenantId.matches("^[a-zA-Z0-9-_]+$")) {
            throw new VertexCacheValidationException("tenantId must be alphanumeric (plus dash/underscore)");
        }
    }
}
