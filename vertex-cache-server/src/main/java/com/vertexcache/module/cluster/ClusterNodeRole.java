package com.vertexcache.module.cluster;

public enum ClusterNodeRole {
    PRIMARY,
    SECONDARY;

    public static boolean isValid(String role) {
        for (ClusterNodeRole r : values()) {
            if (r.name().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    public static ClusterNodeRole from(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Cluster node role is required");
        }
        try {
            return ClusterNodeRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid cluster node role: '" + role + "'");
        }
    }


    public boolean equals(String other) {
        return other != null && this.name().equalsIgnoreCase(other);
    }
}
