package com.vertexcache.client.exception;

public class VertexCacheInternalClientException extends RuntimeException {
    public VertexCacheInternalClientException(String message) {
        super(message);
    }

    public VertexCacheInternalClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
