package com.vertexcache.module.restapi.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.cache.model.DataType;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.auth.model.AuthEntry;
import com.vertexcache.module.restapi.model.ApiResponse;
import com.vertexcache.module.restapi.util.RestApiContextKeys;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public abstract class AbstractRestHandler implements Handler {

    private Context context;
    private JsonObject body;
    private AuthEntry authEntry;

    abstract public void _handle() throws Exception;

    @Override
    public void handle(Context ctx) throws Exception {

        AuthEntry client = getAuth(ctx);

        if (!isWritable(client)) {
            respondError(ctx, 403, "Access denied: write access required");
            return;
        }

        JsonObject body;
        try {
            body = JsonParser.parseString(ctx.body()).getAsJsonObject();
        } catch (Exception e) {
            respondError(ctx, 400, "Invalid JSON body");
            return;
        }

        if (body == null) {
            respondError(ctx, 400, "Empty or malformed JSON");
            return;
        }

        this.authEntry = client;
        this.body = body;
        this.context = ctx;

        this._handle();
    }


    protected AuthEntry getAuth(Context ctx) {
        AuthEntry client = ctx.attribute(RestApiContextKeys.AUTH_ENTRY);
        if (client == null) {
            throw new IllegalStateException("No auth client found in context.");
        }
        return client;
    }

    protected String getStringField(JsonObject obj, String field) {
        return obj.has(field) && !obj.get(field).isJsonNull() ? obj.get(field).getAsString() : null;
    }

    protected DataType parseDataType(String format) {
        if (format == null || format.isBlank()) return DataType.STRING;
        try {
            return DataType.valueOf(format.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new VertexCacheValidationException("Invalid format type: " + format);
        }
    }

    protected void logRequest(String operation) {

        String processedBody = this.body.toString().replace("\"", "\\\"");

        if (processedBody.length() > 500) {
            processedBody = processedBody.substring(0, 500) + "...";
        }

        LogHelper.getInstance().logInfo("[rest:" + this.getAuthEntry().getClientId() + "] Request: " + operation + ", Payload: " + processedBody + " " + this.context.path());
    }

    protected void logResponse(String result) {
        LogHelper.getInstance().logInfo("[rest:" + this.getAuthEntry().getClientId()+ "] Response: " + result);
    }

    protected <T> void respondSuccess(Context ctx, String message, T data) {
        logResponse(message);
        ctx.json(ApiResponse.success(message, data));
    }

    protected void respondSuccess(Context ctx, String message) {
        logResponse(message);
        ctx.json(ApiResponse.success(message));
    }

    protected void respondError(Context ctx, int statusCode, String message) {
        logResponse(message);
        ctx.status(statusCode).json(ApiResponse.error(message));
    }

    protected boolean isReadOnly(AuthEntry auth) {
        return auth.hasRestReadAccess();
    }

    protected boolean isWritable(AuthEntry auth) {
        return auth.hasRestWriteAccess() || auth.isRestAdmin();
    }

    protected AuthEntry getAuthEntry() {
        return this.authEntry;
    }

    protected JsonObject getBody() {
        return this.body;
    }

    protected Context getContext() {
        return this.context;
    }
}
