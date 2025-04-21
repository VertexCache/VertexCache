package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;

import java.util.UUID;

public class UUIDValidator implements Validator {
    private final String value;

    public UUIDValidator(String value) {
        this.value = value;
    }

    @Override
    public void validate() {
        try {
            UUID.fromString(value.trim());
        } catch (Exception e) {
            throw new VertexCacheValidationException("Invalid UUID format: " + value);
        }
    }
}
