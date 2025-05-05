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
