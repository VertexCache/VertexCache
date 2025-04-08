package com.vertexcache.sdk.protocol;

import com.vertexcache.sdk.transport.TcpClient;

public interface Command {
    Command execute(TcpClient client);       // just performs the action
    boolean isSuccess();                  // check success after
    String getResponse();                // parsed or raw string
    String getError();                   // error message if failed
    String getStatusMessage(); // ← add this
}

