package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.ValidatorHandler;
import com.vertexcache.core.validation.VertexCacheValidationException;

import java.util.UUID;

public class UUIDValidator implements ValidatorHandler<String> {
    @Override
    public void validate(String value) {
        try {
            UUID.fromString(value.trim());
        } catch (Exception e) {
            throw new VertexCacheValidationException("Invalid UUID format: " + value);
        }
    }
}
