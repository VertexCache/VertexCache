package com.vertexcache.sdk.transport;

public class TcpClientMock implements TcpClientInterface {

    private final String mockResponse;

    public TcpClientMock(String mockResponse) {
        this.mockResponse = mockResponse;
    }

    @Override
    public String send(String message) {
        return mockResponse;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void close() {
        // do nothing
    }
}

