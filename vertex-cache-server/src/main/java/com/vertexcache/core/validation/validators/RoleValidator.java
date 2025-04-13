package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.ValidatorHandler;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.auth.Role;

public class RoleValidator implements ValidatorHandler<String> {
    @Override
    public void validate(String value) {
        try {
            Role.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new VertexCacheValidationException("Invalid role: " + value + " (must be ADMIN, READ_WRITE, or READ_ONLY)");
        }
    }
}
