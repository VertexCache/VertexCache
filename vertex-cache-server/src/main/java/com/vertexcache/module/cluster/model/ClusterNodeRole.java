package com.vertexcache.module.cluster.model;

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
