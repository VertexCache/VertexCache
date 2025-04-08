package com.vertexcache.sdk.protocol;

public enum CommandType {
    PING("PING"),
    SET("SET"),
    IDX1("idx1"),
    IDX2("idx2");

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
