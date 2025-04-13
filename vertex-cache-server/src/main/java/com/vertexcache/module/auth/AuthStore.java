package com.vertexcache.module.auth;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthStore {
    private final Map<String, AuthEntry> authMap = new ConcurrentHashMap<>();

    public Optional<AuthEntry> get(String clientId) {
        return Optional.ofNullable(authMap.get(clientId));
    }

    public void put(AuthEntry entry) {
        authMap.put(entry.getClientId(), entry);
    }

    public void putAll(Collection<AuthEntry> entries) {
        for (AuthEntry entry : entries) {
            put(entry);
        }
    }

    public List<AuthEntry> list() {
        return new ArrayList<>(authMap.values());
    }

    public void delete(String clientId) {
        authMap.remove(clientId);
    }
}
