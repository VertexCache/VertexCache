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
package com.vertexcache.module.auth.datastore;

import com.vertexcache.module.auth.model.AuthEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AuthStore manages authentication credentials and token mappings within VertexCache.
 * It provides lookup and verification mechanisms for client credentials, access tokens,
 * and associated roles, enabling secure access control enforcement.
 *
 * This class serves as the central authority for authentication state,
 * supporting both static configuration and potential dynamic credential loading.
 */
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
