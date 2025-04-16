package com.vertexcache.module.auth;

import java.util.Objects;

public final class TenantId {

    public static final TenantId DEFAULT = new TenantId("default");

    private final String value;

    public TenantId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TenantId fromString(String val) {
        if (val == null || val.isBlank()) {
            return DEFAULT;
        }
        return new TenantId(val.trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantId tenantId = (TenantId) o;
        return Objects.equals(value, tenantId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
