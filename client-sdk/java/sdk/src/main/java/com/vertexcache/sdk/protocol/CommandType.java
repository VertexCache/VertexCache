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
package com.vertexcache.sdk.protocol;

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
