package com.vertexcache.core.validation;

@FunctionalInterface
public interface Validator {
    void validate() throws VertexCacheValidationException;
}
