/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.server.session;

import com.vertexcache.module.auth.model.Role;
import com.vertexcache.module.auth.model.TenantId;

/**
 * Holds session context information for a connected client.
 *
 * Tracks the client's identifier, associated tenant, and security role.
 * Provides convenience method to check if the client’s role permits execution
 * of a given command.
 */
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
