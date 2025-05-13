package com.vertexcache.module.restapi.model;

public enum TokenHeader {
    AUTHORIZATION,
    NONE,
    UNKNOWN;

    public static TokenHeader from(String value) {
        if (value == null || value.isBlank()) return UNKNOWN;
        String val = value.trim().toLowerCase();
        if (val.equals("authorization")) return AUTHORIZATION;
        if (val.equals("none")) return NONE;
        return UNKNOWN;
    }

    public boolean equals(String other) {
        return other != null && this.name().equalsIgnoreCase(other);
    }
}
