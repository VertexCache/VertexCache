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
 * ClusterNodeRole defines the possible roles a node can assume within the VertexCache cluster.
 * Roles include PRIMARY and SECONDARY, and dictate the responsibilities and behavior
 * of each node in terms of request handling, replication, and failover.
 *
 * This enum is central to cluster coordination and role transition logic,
 * helping enforce correct system behavior under normal and degraded conditions.
 */
public enum ClusterNodeRole {
    PRIMARY,
    SECONDARY,
    UNKNOWN;

    public static ClusterNodeRole from(String role) {
        if (role == null) return UNKNOWN;
        try {
            return ClusterNodeRole.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public static boolean isValid(String role) {
        return from(role) != UNKNOWN;
    }

    public boolean equals(String other) {
        return other != null && this.name().equalsIgnoreCase(other);
    }
}
