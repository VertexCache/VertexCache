package com.vertexcache.module.restapi.model;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS,
    TRACE;

    public static boolean equalsIgnoreCase(String actual, HttpMethod expected) {
        return expected.name().equalsIgnoreCase(actual);
    }

    public static boolean isBodyAllowed(HttpMethod method) {
        return method == POST || method == PUT || method == PATCH;
    }
}