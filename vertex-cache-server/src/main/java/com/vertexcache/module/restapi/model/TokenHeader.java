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
 * Enumeration representing possible HTTP headers used for token authentication.
 *
 * Provides a method to parse a header name string into a TokenHeader enum,
 * and a case-insensitive equality check.
 */
public enum TokenHeader {
    AUTHORIZATION,
    NONE,
    UNKNOWN;

    public static TokenHeader from(String value) {
        if (value == null || value.isBlank()) return UNKNOWN;
        String val = value.trim().toLowerCase();
        if (val.equals("authorization")) return AUTHORIZATION;
        if (val.equals("none")) return NONE;
        return UNKNOWN;
    }

    public boolean equals(String other) {
        return other != null && this.name().equalsIgnoreCase(other);
    }
}
