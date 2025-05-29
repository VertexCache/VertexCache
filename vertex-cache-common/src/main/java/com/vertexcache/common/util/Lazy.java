/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.common.util;

import java.util.function.Supplier;

/**
 * Lightweight utility for lazy initialization of values.
 *
 * This class provides:
 *  - A thread-safe mechanism for computing a value only once on first access
 *  - Support for deferred initialization using a Supplier<T>
 *  - Simple caching behavior without third-party libraries or full memoization frameworks
 *
 * Useful for performance optimization, deferring expensive computations,
 * or lazy-loading internal resources.
 */
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
