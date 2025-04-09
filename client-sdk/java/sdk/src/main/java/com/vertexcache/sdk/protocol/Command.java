package com.vertexcache.sdk.protocol;

import com.vertexcache.sdk.transport.TcpClientInterface;

public interface Command {
    Command execute(TcpClientInterface client);
    boolean isSuccess();
    String getResponse();
    String getError();
    String getStatusMessage();
}

