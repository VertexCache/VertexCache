package com.vertexcache.client.transport;

public interface TcpClientInterface {
    String send(String message);
    boolean isConnected();
    void close();
}
