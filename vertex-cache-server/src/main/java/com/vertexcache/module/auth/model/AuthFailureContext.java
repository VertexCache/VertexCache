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

/**
 * AuthFailureContext captures contextual information about a failed authentication attempt
 * within VertexCache. It includes details such as the reason for failure, client identifier,
 * and the source of the request, which can be used for logging, auditing, or alerting.
 *
 * This class helps provide meaningful diagnostics and traceability for security-related
 * issues, enabling better analysis of authentication failures across the system.
 */
public class AuthFailureContext {
    private final String clientId;
    private final String ipAddress;
    private final String attemptedRole;
    private final String reason;

    public AuthFailureContext(String clientId, String ipAddress, String attemptedRole, String reason) {
        this.clientId = clientId;
        this.ipAddress = ipAddress;
        this.attemptedRole = attemptedRole;
        this.reason = reason;
    }

    public String getClientId() {
        return clientId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getAttemptedRole() {
        return attemptedRole;
    }

    public String getReason() {
        return reason;
    }
}

