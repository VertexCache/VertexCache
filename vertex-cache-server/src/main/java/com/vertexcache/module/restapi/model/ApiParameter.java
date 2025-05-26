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

public enum ApiParameter {
    KEY("key"),
    VALUE("value"),
    FORMAT("format"),
    IDX1("idx1"),
    IDX2("idx2"),
    CLIENT_ID("clientId"),
    TOKEN("token");

    private final String name;

    ApiParameter(String name) {
        this.name = name;
    }

    public String value() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
