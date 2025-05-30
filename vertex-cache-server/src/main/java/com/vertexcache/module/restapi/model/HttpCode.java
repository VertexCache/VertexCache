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
 * Enumeration of standard HTTP status codes used by the REST API.
 *
 * Categorized into:
 * - 2xx Success responses
 * - 4xx Client error responses
 * - 5xx Server error responses
 *
 * Provides numeric code values and string representation for each status.
 */
public enum HttpCode {

    // --- 2xx Success ---
    OK(200),
    CREATED(201),
    NO_CONTENT(204),

    // --- 4xx Client Errors ---
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    CONFLICT(409),
    PAYLOAD_TOO_LARGE(413),
    UNSUPPORTED_MEDIA_TYPE(415),
    UNPROCESSABLE_ENTITY(422),
    TOO_MANY_REQUESTS(429),

    // --- 5xx Server Errors ---
    INTERNAL_SERVER_ERROR(500),
    NOT_IMPLEMENTED(501),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503),
    GATEWAY_TIMEOUT(504);

    private final int code;

    HttpCode(int code) {
        this.code = code;
    }

    public int value() {
        return this.code;
    }

    public String toString() {
        return String.valueOf(this.value());
    }
}
