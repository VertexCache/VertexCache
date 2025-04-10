package com.vertexcache.core.auth;

public class AuthEntry {
    private String clientId;
    private String token;
    private String tenantId;
    private Role role;

    public AuthEntry() {}

    public AuthEntry(String clientId, String token, String tenantId, Role role) {
        this.clientId = clientId;
        this.token = token;
        this.tenantId = tenantId;
        this.role = role;
    }

    public String getClientId() { return clientId; }
    public String getToken() { return token; }
    public String getTenantId() { return tenantId; }
    public Role getRole() { return role; }
}


