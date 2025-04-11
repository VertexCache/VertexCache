package com.vertexcache.module.auth;

import org.mapdb.*;
import java.util.concurrent.ConcurrentMap;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

public class AuthStore {
    private DB db;
    private ConcurrentMap<String, AuthEntry> authMap;

    public AuthStore(String filePath) {
        db = DBMaker.fileDB(filePath).transactionEnable().make();
        authMap = db.hashMap("auth", Serializer.STRING, Serializer.JAVA).createOrOpen();
    }

    public Optional<AuthEntry> get(String clientId) {
        return Optional.ofNullable(authMap.get(clientId));
    }

    public void put(AuthEntry entry) {
        authMap.put(entry.getClientId(), entry);
        db.commit();
    }

    public void delete(String clientId) {
        authMap.remove(clientId);
        db.commit();
    }

    public List<AuthEntry> list() {
        return authMap.values().stream().collect(Collectors.toList());
    }

    public void close() {
        db.close();
    }
}
