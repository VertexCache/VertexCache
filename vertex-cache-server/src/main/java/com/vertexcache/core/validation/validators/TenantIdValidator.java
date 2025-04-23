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

        if (Config.getInstance().getConfigAuthWithTenant().isTenantKeyPrefixingEnabled() &&
                RESERVED_DEFAULT.equalsIgnoreCase(tenantId.trim())) {
            throw new VertexCacheValidationException("'default' is a reserved tenant ID and cannot be used when tenant key prefixing is enabled");
        }

        if (!tenantId.matches("^[a-zA-Z0-9-_]+$")) {
            throw new VertexCacheValidationException("tenantId must be alphanumeric (plus dash/underscore)");
        }
    }
}
