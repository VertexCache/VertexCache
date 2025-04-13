package com.vertexcache.core.validation;

public class VertexCacheValidationException extends RuntimeException {
    public VertexCacheValidationException(String message) {
        super(message);
    }

    public VertexCacheValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
