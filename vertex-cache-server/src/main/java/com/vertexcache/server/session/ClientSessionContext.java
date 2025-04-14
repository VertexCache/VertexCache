package com.vertexcache.server.session;

import com.vertexcache.module.auth.Role;
import com.vertexcache.module.auth.TenantId;

public class ClientSessionContext {
    private String clientId;
    private TenantId tenantId;
    private Role role;

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public TenantId getTenantId() { return tenantId; }
    public void setTenantId(TenantId tenantId) { this.tenantId = tenantId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean hasRole(String command) {
        return role != null && role.canExecute(command);
    }
}
