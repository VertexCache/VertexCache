package com.vertexcache.module.cluster.enums;

public enum ClusterNodeStatus {
    ACTIVE,
    STANDBY;

    public static boolean isValid(String status) {
        for (ClusterNodeStatus s : values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    public static ClusterNodeStatus from(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Cluster node status is required.");
        }
        try {
            return ClusterNodeStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid cluster node status: '" + status + "'. Must be ACTIVE or STANDBY.");
        }
    }

    public boolean equals(String other) {
        return other != null && this.name().equalsIgnoreCase(other);
    }
}
