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

    public boolean hasRestReadAccess() {
        return role == Role.REST_API_READ_ONLY || role == Role.REST_API_READ_WRITE;
    }

    public boolean hasRestWriteAccess() {
        return role == Role.REST_API_READ_WRITE;
    }

    public boolean isAlertBot() {
        return role == ALERT_BOT_READ_ONLY;
    }

    public String getClientId() { return clientId; }
    public TenantId getTenantId() { return tenantId; }
    public Role getRole() { return role; }
    public String getToken() { return token; }
}
