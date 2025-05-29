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
package com.vertexcache.common.config;

/**
 * Exception thrown when a configuration-related error occurs during VertexCache initialization or runtime.
 *
 * This exception is used to indicate:
 *  - Missing, malformed, or invalid configuration values
 *  - Issues parsing environment variables or configuration files
 *  - Any condition that prevents successful configuration loading or validation
 *
 * Typically thrown by config loaders, validators, or startup routines.
 * Intended to surface fatal misconfiguration early during application boot.
 */
public class VertexCacheConfigException extends RuntimeException {
    public VertexCacheConfigException(String message) {
        super(message);
    }
    public VertexCacheConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
