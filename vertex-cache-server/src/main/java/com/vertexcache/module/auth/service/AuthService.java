package com.vertexcache.module.auth.service;

import com.vertexcache.module.auth.datastore.AuthStore;
import com.vertexcache.module.auth.model.AuthEntry;

import java.util.Optional;

public class AuthService {
    private final AuthStore store;

    public AuthService(AuthStore store) {
        this.store = store;
    }

    public Optional<AuthEntry> authenticate(String clientId, String token) {
        Optional<AuthEntry> entry = store.get(clientId);
        return entry.isPresent() && entry.get().getToken().equals(token)
                ? entry
                : Optional.empty();
    }
}
