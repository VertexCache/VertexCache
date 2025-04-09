package com.vertexcache.sdk.transport;

/*
 * Did this mainly out of creating a manual Inversion of Control (IoC) or dependency injection
 * instead using a framework, see TcpClientMock under unit tests
 */
public interface TcpClientInterface {
    String send(String message);
    boolean isConnected();
    void close();
}
