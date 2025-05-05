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
