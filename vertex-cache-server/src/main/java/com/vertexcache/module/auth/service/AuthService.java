package com.vertexcache.module.auth.service;

import com.vertexcache.module.auth.datastore.AuthStore;
import com.vertexcache.module.auth.model.AuthEntry;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {

    private static AuthService instance;

    private final AuthStore store;

    // Token-indexed lookup map for constant-time O(1) access.
    // This avoids scanning all clients (which would be O(n)) when authenticating
    // REST API or SDK clients by token alone.
    private final Map<String, AuthEntry> tokenIndex = new ConcurrentHashMap<>();

    private AuthService(AuthStore store) {
        this.store = store;
        for (AuthEntry entry : store.list()) {
            tokenIndex.put(entry.getToken(), entry);
        }
    }

    public static void initialize(AuthStore store) {
        if (instance == null) {
            instance = new AuthService(store);
        } else {
            throw new IllegalStateException("AuthService has already been initialized");
        }
    }

    public static AuthService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AuthService has not been initialized");
        }
        return instance;
    }

    public Optional<AuthEntry> authenticate(String clientId, String token) {
        Optional<AuthEntry> entry = store.get(clientId);
        return entry.isPresent() && token.equals(entry.get().getToken())
                ? entry
                : Optional.empty();
    }

    /**
     * Retrieve an authenticated client using only a token (for REST/API clients).
     * Fast O(1) lookup using tokenIndex map. This is more scalable than scanning all entries.
     */
    public AuthEntry getClientByToken(String token) {
        return tokenIndex.get(token);
    }

    public void put(AuthEntry entry) {
        store.put(entry);
        tokenIndex.put(entry.getToken(), entry);
    }

    public void loadAll(Iterable<AuthEntry> entries) {
        store.putAll((Collection<AuthEntry>) entries);
        tokenIndex.clear();
        for (AuthEntry entry : entries) {
            tokenIndex.put(entry.getToken(), entry);
        }
    }

    public void delete(String clientId) {
        store.get(clientId).ifPresent(entry -> tokenIndex.remove(entry.getToken()));
        store.delete(clientId);
    }
}
