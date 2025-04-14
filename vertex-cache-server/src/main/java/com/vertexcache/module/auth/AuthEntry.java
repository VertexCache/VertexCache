package com.vertexcache.module.auth;

public class AuthEntry {
    private final String clientId;
    private final TenantId tenantId;
    private final Role role;
    private final String token;

    public AuthEntry(String clientId, TenantId tenantId, Role role, String token) {
        this.clientId = clientId;
        this.tenantId = tenantId;
        this.role = role;
        this.token = token;
    }

    public String getClientId() { return clientId; }
    public TenantId getTenantId() { return tenantId; }
    public Role getRole() { return role; }
    public String getToken() { return token; }
}
