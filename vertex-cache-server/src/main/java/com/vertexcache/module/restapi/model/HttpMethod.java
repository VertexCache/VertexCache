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
package com.vertexcache.module.restapi.model;

/**
 * Enumeration of standard HTTP methods supported by the REST API.
 *
 * Includes utility methods to compare method names ignoring case and to check
 * if a method typically allows a request body (POST, PUT, PATCH).
 */
public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS,
    TRACE;

    public static boolean equalsIgnoreCase(String actual, HttpMethod expected) {
        return expected.name().equalsIgnoreCase(actual);
    }

    public static boolean isBodyAllowed(HttpMethod method) {
        return method == POST || method == PUT || method == PATCH;
    }
}