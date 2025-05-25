package com.vertexcache.module.auth.model;

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

