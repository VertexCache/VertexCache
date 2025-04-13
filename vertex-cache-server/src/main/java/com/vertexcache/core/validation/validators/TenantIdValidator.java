package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.ValidatorHandler;
import com.vertexcache.core.validation.VertexCacheValidationException;

public class TenantIdValidator implements ValidatorHandler<String> {
    @Override
    public void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new VertexCacheValidationException("Tenant ID cannot be blank");
        }
        if (!value.matches("^[a-zA-Z0-9._-]+$")) {
            throw new VertexCacheValidationException("Tenant ID must be alphanumeric with -, _, . allowed");
        }
    }
}
