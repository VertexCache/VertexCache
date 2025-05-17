package com.vertexcache.module.restapi.model;

public enum ApiParameter {
    KEY("key"),
    VALUE("value"),
    FORMAT("format"),
    IDX1("idx1"),
    IDX2("idx2"),
    CLIENT_ID("clientId"),
    TOKEN("token");

    private final String name;

    ApiParameter(String name) {
        this.name = name;
    }

    public String value() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
