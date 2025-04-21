package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;

public class ClientIdValidator implements Validator {
    private final String value;

    public ClientIdValidator(String value) {
        this.value = value;
    }

    @Override
    public void validate() {
        if (value == null || value.isBlank()) {
            throw new VertexCacheValidationException("Client ID cannot be blank");
        }
        if (!value.matches("^[a-zA-Z0-9._-]+$")) {
            throw new VertexCacheValidationException("Client ID must be alphanumeric with -, _, . allowed");
        }
    }
}
