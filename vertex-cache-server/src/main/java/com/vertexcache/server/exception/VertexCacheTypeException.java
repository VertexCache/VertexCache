package com.vertexcache.server.exception;

public class VertexCacheTypeException extends Exception {
    public VertexCacheTypeException() {
        super();
    }

    public VertexCacheTypeException(String message) {
        super(message);
    }

    public VertexCacheTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public VertexCacheTypeException(Throwable cause) {
        super(cause);
    }
}
