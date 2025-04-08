package com.vertexcache.sdk.result;

public class VertexCacheSdkException extends RuntimeException {
    public VertexCacheSdkException(String message) {
        super(message);
    }

    public VertexCacheSdkException(String message, Throwable cause) {
        super(message, cause);
    }
}
