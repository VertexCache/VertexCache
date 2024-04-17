package com.vertexcache.server.exception;

public class VertexCacheException extends Exception {
    public VertexCacheException() {
        super();
    }

    public VertexCacheException(String message) {
        super(message);
    }

    public VertexCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public VertexCacheException(Throwable cause) {
        super(cause);
    }
}
