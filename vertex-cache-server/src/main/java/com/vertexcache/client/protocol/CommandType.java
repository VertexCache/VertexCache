package com.vertexcache.client.protocol;

public enum CommandType {
    PING("PING");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    public String keyword() {
        return keyword;
    }

    @Override
    public String toString() {
        return keyword;
    }
}

