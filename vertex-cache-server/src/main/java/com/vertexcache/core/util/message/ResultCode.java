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
package com.vertexcache.core.util.message;

import java.util.Locale;

public enum ResultCode {

    // --- Success Codes ---
    OK("0", "Success"),
    PONG("1", "PONG"),
    CACHE_HIT("2", "Cache hit"),
    VALUE_SET("3", "Value set successfully"),
    VALUE_DELETED("4", "Value deleted successfully"),

    // --- Client Errors ---
    MISSING_CLIENT_ID("100", "Missing client ID"),
    MISSING_AUTH_TOKEN("101", "Missing authorization token"),
    UNAUTHORIZED("102", "Unauthorized access"),
    UNKNOWN_COMMAND("103", "Unknown command"),
    INVALID_COMMAND("104", "Invalid command syntax"),
    VALIDATION_ERROR("105", "Validation failed"),
    KEY_NOT_FOUND("106", "Key not found"),
    KEY_REQUIRED("107", "Key is required"),
    VALUE_REQUIRED("108", "Value is required"),
    FORMAT_INVALID("109", "Unsupported or invalid format"),
    IDX2_REQUIRES_IDX1("110", "idx2 requires idx1 to be provided"),

    // --- Server/Internal Errors ---
    INTERNAL_ERROR("900", "Internal server error"),
    MODULE_DISABLED("901", "Module is not enabled"),
    NOT_IMPLEMENTED("902", "Feature not implemented"),
    TIMEOUT("903", "Request timed out");

    private final String code;
    private final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getLocalizedMessage(Locale locale) {
        // Placeholder for future i18n support
        return message;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", code, message);
    }
}
