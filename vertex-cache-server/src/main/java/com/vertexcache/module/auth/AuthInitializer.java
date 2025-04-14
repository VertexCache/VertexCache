package com.vertexcache.module.auth;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.ValidationBatch;
import com.vertexcache.core.validation.validators.ClientIdValidator;
import com.vertexcache.core.validation.validators.RoleValidator;
import com.vertexcache.core.validation.validators.TenantIdValidator;
import com.vertexcache.core.validation.validators.UUIDValidator;

import java.util.List;

public class AuthInitializer {

    public static AuthService initializeFromEnv() {
        List<String> rawEntries = Config.getInstance().getRawAuthClientEntries();
        AuthStore store = new AuthStore();

        for (String line : rawEntries) {
            String[] parts = line.split(":");
            if (parts.length != 4) {
                throw new VertexCacheAuthInitializationException("Invalid auth_client entry (must have 4 parts): " + line);
            }

            String clientId = parts[0].trim();
            String tenantId = parts[1].trim();
            String roleRaw = parts[2].trim();
            String token = parts[3].trim();

            ValidationBatch batch = new ValidationBatch();
            batch.check("clientId", new ClientIdValidator(), clientId);
            batch.check("tenantId", new TenantIdValidator(), tenantId);
            batch.check("role", new RoleValidator(), roleRaw);
            batch.check("token", new UUIDValidator(), token);

            if (batch.hasErrors()) {
                throw new VertexCacheAuthInitializationException("Invalid auth_client [" + line + "]: " + batch.getSummary());
            }

            try {
                Role role = Role.valueOf(roleRaw.toUpperCase());
                store.put(new AuthEntry(clientId, TenantId.fromString(tenantId), Role.fromString(roleRaw), token));
            } catch (Exception e) {
                throw new VertexCacheAuthInitializationException("Failed to register auth_client: " + line);
            }
        }

        if (store.list().isEmpty()) {
            throw new VertexCacheAuthInitializationException("No valid auth clients found in .env (auth_client_*)");
        }

        return new AuthService(store);
    }
}
