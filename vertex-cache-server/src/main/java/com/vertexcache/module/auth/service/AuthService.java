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
package com.vertexcache.module.auth.service;

import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.auth.datastore.AuthStore;
import com.vertexcache.module.auth.listener.AuthFailureListener;
import com.vertexcache.module.auth.model.AuthEntry;
import com.vertexcache.module.auth.model.AuthFailureContext;
import com.vertexcache.module.metric.MetricModule;
import com.vertexcache.module.metric.service.MetricAccess;
import com.vertexcache.module.smart.SmartModule;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {

    private static AuthService instance;
    private final AuthStore store;
    private static AuthFailureListener authFailureListener;

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

    /*
    public Optional<AuthEntry> authenticate(String clientId, String token) {
        Optional<AuthEntry> entry = store.get(clientId);
        return entry.isPresent() && token.equals(entry.get().getToken())
                ? entry
                : Optional.empty();
    }*/

    public Optional<AuthEntry> authenticate(String clientId, String token) {
        Optional<AuthEntry> entry = store.get(clientId);

        if (entry.isEmpty()) {
            notifyAuthFailure(clientId, null, "UNKNOWN_CLIENT_ID");
            return Optional.empty();
        }

        AuthEntry authEntry = entry.get();
        if (!token.equals(authEntry.getToken())) {
            notifyAuthFailure(clientId, authEntry.getRole().toString(), "INVALID_CREDENTIALS");
            return Optional.empty();
        }

        return Optional.of(authEntry);
    }

    private void notifyAuthFailure(String clientId, String role, String reason) {
        if (Config.getInstance().getSmartConfigLoader().isEnableSmart() && Config.getInstance().getSmartConfigLoader().isEnableSmartUnauthorizedAccessAlert()) {
            initAuthFailerListener();
            AuthFailureContext context = new AuthFailureContext(clientId, null, role, reason);
            authFailureListener.onAuthFailure(context);
        }
    }

    /**
     * Retrieve an authenticated client using only a token (for REST/API clients).
     * Fast O(1) lookup using tokenIndex map. This is more scalable than scanning all entries.
     */
    public AuthEntry authenticateByToken(String token) {
        AuthEntry authEntry = tokenIndex.get(token);
        if(authEntry == null && Config.getInstance().getSmartConfigLoader().isEnableSmart() && Config.getInstance().getSmartConfigLoader().isEnableSmartUnauthorizedAccessAlert()) {
            initAuthFailerListener();
            authFailureListener.onInvalidToken(token);
            return null;
        }
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

    private void initAuthFailerListener() {
        if(authFailureListener == null) {
            Optional<SmartModule> optSmartModule = ModuleRegistry.getInstance().getModule(SmartModule.class);
            authFailureListener = (AuthFailureListener) (optSmartModule.get()).getUnauthorizedAccessAlertService();
        }
    }
}
