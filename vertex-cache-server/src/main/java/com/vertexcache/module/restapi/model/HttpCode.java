package com.vertexcache.module.restapi.model;

public enum HttpCode {

    // --- 2xx Success ---
    OK(200),
    CREATED(201),
    NO_CONTENT(204),

    // --- 4xx Client Errors ---
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    CONFLICT(409),
    PAYLOAD_TOO_LARGE(413),
    UNSUPPORTED_MEDIA_TYPE(415),
    UNPROCESSABLE_ENTITY(422),
    TOO_MANY_REQUESTS(429),

    // --- 5xx Server Errors ---
    INTERNAL_SERVER_ERROR(500),
    NOT_IMPLEMENTED(501),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503),
    GATEWAY_TIMEOUT(504);

    private final int code;

    HttpCode(int code) {
        this.code = code;
    }

    public int value() {
        return this.code;
    }

    public String toString() {
        return String.valueOf(this.value());
    }
}
