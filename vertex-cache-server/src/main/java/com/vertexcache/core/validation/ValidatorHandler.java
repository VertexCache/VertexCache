package com.vertexcache.core.validation;

@FunctionalInterface
public interface ValidatorHandler<T> {
    void validate(T value) throws VertexCacheValidationException;

    static <T> void run(ValidatorHandler<T> validator, T value) {
        validator.validate(value);
    }
}
