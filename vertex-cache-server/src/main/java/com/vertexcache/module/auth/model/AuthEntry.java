package com.vertexcache.module.auth.model;

import static com.vertexcache.module.auth.model.Role.*;

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

    public boolean isRestAdmin() { return role == REST_API_ADMIN; }

    public boolean hasRestReadAccess() {
        return role == Role.REST_API_READ_ONLY || role == Role.REST_API_READ_WRITE || role == Role.REST_API_ADMIN;
    }

    public boolean hasRestWriteAccess() {
        return role == Role.REST_API_READ_WRITE || role == Role.REST_API_ADMIN;
    }

    public boolean isAlertBot() {
        return role == ALERT_BOT_READ_ONLY;
    }

    public String getClientId() { return clientId; }
    public TenantId getTenantId() { return tenantId; }
    public Role getRole() { return role; }
    public String getToken() { return token; }
}
