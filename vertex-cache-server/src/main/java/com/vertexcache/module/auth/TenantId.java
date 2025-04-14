package com.vertexcache.module.auth;

public enum TenantId {
    DEFAULT("default");

    private final String value;

    TenantId(String value) {
        this.value = value;
    }

    public String getValue() { return value; }

    public static TenantId fromString(String val) {
        if (val == null) return DEFAULT;
        for (TenantId t : values()) {
            if (t.value.equalsIgnoreCase(val.trim())) {
                return t;
            }
        }
        return DEFAULT;
    }
}
