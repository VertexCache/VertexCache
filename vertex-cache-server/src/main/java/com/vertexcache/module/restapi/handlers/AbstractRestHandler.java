package com.vertexcache.module.restapi.handlers;

import com.vertexcache.module.auth.model.AuthEntry;
import com.vertexcache.module.restapi.model.ApiResponse;
import com.vertexcache.module.restapi.util.RestApiContextKeys;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public abstract class AbstractRestHandler implements Handler {

    protected AuthEntry getAuth(Context ctx) {
        AuthEntry client = ctx.attribute(RestApiContextKeys.AUTH_ENTRY);
        if (client == null) {
            throw new IllegalStateException("No auth client found in context.");
        }
        return client;
    }

    protected <T> void respondSuccess(Context ctx, String message, T data) {
        ctx.json(ApiResponse.success(message, data));
    }

    protected void respondSuccess(Context ctx, String message) {
        ctx.json(ApiResponse.success(message));
    }

    protected void respondError(Context ctx, int statusCode, String message) {
        ctx.status(statusCode).json(ApiResponse.error(message));
    }

    protected boolean isReadOnly(AuthEntry auth) {
        return auth.hasRestReadAccess();
    }

    protected boolean isWritable(AuthEntry auth) {
        return auth.hasRestWriteAccess() || auth.isRestAdmin();
    }
}
