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
package com.vertexcache.server.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe registry managing active client sessions.
 *
 * Provides static methods to register, unregister, query, and list client session contexts
 * by connection identifier. Supports checking if a client is currently connected.
 *
 * Uses a concurrent hash map to allow safe concurrent access.
 */
public class SessionRegistry {

    private static final Map<String, ClientSessionContext> sessions = new ConcurrentHashMap<>();

    private SessionRegistry() {
        // static utility only
    }

    public static void register(String connectionId, ClientSessionContext session) {
        sessions.put(connectionId, session);
    }

    public static void unregister(String connectionId) {
        sessions.remove(connectionId);
    }

    public static ClientSessionContext get(String connectionId) {
        return sessions.get(connectionId);
    }

    public static Map<String, ClientSessionContext> listAll() {
        return sessions;
    }

    public static int count() {
        return sessions.size();
    }

    public static boolean isConnected(String clientId) {
        return sessions.values().stream().anyMatch(s -> clientId.equals(s.getClientId()));
    }

    public static void clearAll() {
        sessions.clear();
    }
}
