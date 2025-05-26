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

public enum ClusterNodeHealthStatus {
    ACTIVE,
    STANDBY,
    DOWN,
    UNKNOWN;

    public static ClusterNodeHealthStatus from(String status) {
        if (status == null || status.isBlank()) {
            return UNKNOWN;
        }
        try {
            return ClusterNodeHealthStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public static boolean isValid(String status) {
        return from(status) != UNKNOWN;
    }

    public boolean equals(String other) {
        return other != null && this.name().equalsIgnoreCase(other);
    }
}
