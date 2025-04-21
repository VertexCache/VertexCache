package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.auth.Role;

public class RoleValidator implements Validator {
    private final String value;

    public RoleValidator(String value) {
        this.value = value;
    }

    @Override
    public void validate() {
        try {
            Role.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new VertexCacheValidationException("Invalid role: " + value + " (must be ADMIN, READ_WRITE, or READ_ONLY)");
        }
    }
}
