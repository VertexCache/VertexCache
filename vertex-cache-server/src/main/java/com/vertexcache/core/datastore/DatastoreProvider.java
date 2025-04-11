package com.vertexcache.core.datastore;

public interface DatastoreProvider {
    void connect();
    void close();
    boolean isConnected();
}
