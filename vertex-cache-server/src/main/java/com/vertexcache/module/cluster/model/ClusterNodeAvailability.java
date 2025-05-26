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
package com.vertexcache.module.cluster.model;

public enum ClusterNodeAvailability {
    ENABLED,
    DISABLED,
    UNKNOWN;

    public static ClusterNodeAvailability from(String value) {
        if (value == null || value.isBlank()) return UNKNOWN;
        String val = value.trim().toLowerCase();
        if (val.equals("true")) return ENABLED;
        if (val.equals("false")) return DISABLED;
        return UNKNOWN;
    }

    public boolean isEnabled() {
        return this == ENABLED;
    }

    public boolean equals(String other) {
        return other != null && this.name().equalsIgnoreCase(other);
    }
}
