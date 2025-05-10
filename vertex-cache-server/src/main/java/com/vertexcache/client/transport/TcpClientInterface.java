package com.vertexcache.client.transport;

import com.vertexcache.client.protocol.CommandFailureHandler;

public interface TcpClientInterface {
    String send(String message);
    boolean isConnected();
    void close();
    void setCommandFailureHandler(CommandFailureHandler callback);
}
