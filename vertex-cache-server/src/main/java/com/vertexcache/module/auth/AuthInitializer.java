package com.vertexcache.module.auth;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.exception.VertexCacheAuthInitializationException;

import java.util.*;

public class AuthInitializer {

    public static AuthService initializeFromEnv() {
        List<String> rawEntries = Config.getInstance().getRawAuthClientEntries();

        AuthStore store = new AuthStore();
        for (String line : rawEntries) {
            String[] parts = line.split(":");
            if (parts.length != 4) {
                throw new VertexCacheAuthInitializationException("Invalid auth_client entry: " + line);
                //continue;
            }

            String clientId = parts[0].trim();
            String tenantId = parts[1].trim();
            String roleRaw = parts[2].trim();
            String token = parts[3].trim();

            try {
                Role role = Role.valueOf(roleRaw.toUpperCase());
                UUID.fromString(token); // Validate token format
                store.put(new AuthEntry(clientId, token, tenantId, role));
            } catch (Exception e) {
                throw new VertexCacheAuthInitializationException("Failed to load auth_client: " + line);
            }
        }

        if (store.list().isEmpty()) {
            throw new VertexCacheAuthInitializationException("No valid auth clients found. Check your .env 'auth_client_*' entries.");
        }

        return new AuthService(store);
    }
}
