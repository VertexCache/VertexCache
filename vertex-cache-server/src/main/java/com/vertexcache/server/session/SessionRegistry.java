package com.vertexcache.server.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
