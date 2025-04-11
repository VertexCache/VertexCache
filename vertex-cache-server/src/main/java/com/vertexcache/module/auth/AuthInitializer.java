package com.vertexcache.module.auth;

import java.util.List;

public class AuthInitializer {
    public static AuthService initialize(String seedFilePath, String dbPath) {
        AuthStore store = new AuthStore(dbPath);

        List<AuthEntry> seeded = AuthSeedLoader.loadFromJsonFile(seedFilePath);
        for (AuthEntry entry : seeded) {
            store.put(entry); // skips duplicates by clientId
        }

        return new AuthService(store);
    }
}

