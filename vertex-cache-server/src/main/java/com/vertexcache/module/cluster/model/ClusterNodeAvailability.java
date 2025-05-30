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

/**
 * ClusterNodeAvailability defines the availability states of a node within the VertexCache cluster.
 * It indicates whether a node is currently reachable and eligible for participation in
 * cluster operations such as coordination, promotion, or failover.
 *
 * Typical states may include UP, DOWN, or UNKNOWN, and are used during health checks,
 * peer state evaluations, and cluster coordination decisions.
 */
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
