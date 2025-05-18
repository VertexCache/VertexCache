package com.vertexcache.module.restapi.exception;

public class VertexCacheRestApiException extends Exception {
    public VertexCacheRestApiException() {
        super();
    }

    public VertexCacheRestApiException(String message) {
        super(message);
    }

    public VertexCacheRestApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public VertexCacheRestApiException(Throwable cause) {
        super(cause);
    }
}
