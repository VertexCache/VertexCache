package com.vertexcache.client.protocol;

import com.vertexcache.client.transport.TcpClientInterface;

public interface Command {
    Command execute(TcpClientInterface client);
    boolean isSuccess();
    String getResponse();
    String getError();
    String getStatusMessage();
}
