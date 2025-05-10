package com.vertexcache.client.protocol;

public interface CommandFailureHandler {
    void onFailedConnect(String host, int port);
    void onFailedSend(String command, Throwable cause);
}
