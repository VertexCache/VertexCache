package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.VertexCacheValidationException;

public class KeyValidator {

    private final String fieldName;
    private final String value;

    public KeyValidator(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public void validate() {
        if (value == null || value.isBlank()) {
            throw new VertexCacheValidationException(fieldName + " must not be blank");
        }

        if (value.length() > 255) {
            throw new VertexCacheValidationException(fieldName + " exceeds maximum length (255)");
        }

        if (!value.matches("^[a-zA-Z0-9_-]+$")) {
            throw new VertexCacheValidationException(fieldName + " contains invalid characters");
        }
    }
}
