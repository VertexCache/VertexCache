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
package com.vertexcache.module.restapi.exception;

/**
 * Exception class for handling REST API-related errors in VertexCache.
 *
 * Used to represent various failure scenarios such as invalid input,
 * processing errors, or unexpected conditions in REST request handling.
 * Supports multiple constructors for flexibility in exception chaining.
 */
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
