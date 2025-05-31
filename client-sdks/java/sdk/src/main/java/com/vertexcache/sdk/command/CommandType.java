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
package com.vertexcache.sdk.command;

/**
 * Enum representing the different types of commands supported by the VertexCache SDK.
 *
 * Each command type corresponds to a specific cache operation or internal SDK operation,
 * such as GET, SET, DELETE, or IDENT (used for client identification and authentication).
 *
 * This enum is used throughout the SDK to identify and validate command behavior,
 * facilitate routing, and enforce permission checks based on role capabilities.
 */
public enum CommandType {
    PING("PING"),
    SET("SET"),
    DEL("DEL"),
    IDX1("IDX1"),
    IDX2("IDX2");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    public String keyword() {
        return keyword;
    }

    @Override
    public String toString() {
        return keyword;
    }
}
