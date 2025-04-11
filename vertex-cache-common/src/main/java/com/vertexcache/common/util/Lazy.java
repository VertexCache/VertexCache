package com.vertexcache.common.util;

import java.util.function.Supplier;

public class Lazy<T> {
    private final Supplier<T> initializer;
    private T value;

    public Lazy(Supplier<T> initializer) {
        this.initializer = initializer;
    }

    public T get() {
        if (value == null) {
            value = initializer.get();
        }
        return value;
    }
}
