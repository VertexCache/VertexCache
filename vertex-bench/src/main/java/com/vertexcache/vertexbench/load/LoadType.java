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
package com.vertexcache.vertexbench.load;

import com.vertexcache.vertexbench.exception.VertexBenchException;

public enum LoadType {
    DEL_ONLY("DEL-Only", "del-only"),
    GET_ONLY("GET-Only", "get-only"),
    MIXED("Mixed (Configurable GET/SET/DEL)", "mixed"),
    OPEN_LOOP("Open-Loop", "open-loop"),
    SECONDARY_INDEX("Secondary-Index-Lookup", "secondary-index"),
    SET_ONLY("SET-Only", "set-only"),
    TERTIARY_INDEX("Tertiary-Index-Lookup", "tertiray-index"),
    ;

    private final String title;
    private final String key;

    LoadType(String title, String key) {
        this.title = title;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public String getKey() {
        return key;
    }

    public static LoadType fromKey(String key) {
        for (LoadType type : values()) {
            if (type.key.equalsIgnoreCase(key)) {
                return type;
            }
        }
        throw new VertexBenchException("Unknown test name: " + key);
    }
}
