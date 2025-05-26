/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.module.restapi.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.cache.model.DataType;
import com.vertexcache.core.util.message.ResultCode;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.auth.model.AuthEntry;
import com.vertexcache.module.restapi.model.ApiResponse;
import com.vertexcache.module.restapi.model.HttpCode;
import com.vertexcache.module.restapi.model.HttpMethod;
import com.vertexcache.module.restapi.model.RestApiContextKeys;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public abstract class AbstractRestHandler implements Handler {

    private static final int MAX_BODY_LOG_OUTPUT = 500;

    private Context context;
    private JsonObject body;
    private AuthEntry authEntry;

    abstract public void _handle() throws Exception;

    @Override
    public void handle(Context ctx) throws Exception {
        AuthEntry client = getAuth(ctx);
        JsonObject body = null;
        if (!ctx.method().name().equalsIgnoreCase(HttpMethod.GET.name()) && !ctx.method().name().equalsIgnoreCase(HttpMethod.DELETE.name())) {
            try {
                body = JsonParser.parseString(ctx.body()).getAsJsonObject();
                if (!body.isEmpty()) {
                    body = normalizeKeys(body);
                }
            } catch (Exception e) {
                respondBadRequest("Invalid JSON body");
                return;
            }
            if (body == null) {
                respondBadRequest("Empty or malformed JSON");
                return;
            }
            this.body = body;
        }
        this.authEntry = client;
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

    protected String getPathParam(String... possibleNames) {
        for (String name : possibleNames) {
            try {
                String value = this.context.pathParam(name);
                if (value != null) return value;
            } catch (Exception ignored) {}
        }
        throw new IllegalArgumentException("Missing expected path parameter: " + String.join(" or ", possibleNames));
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

    protected JsonObject normalizeKeys(JsonObject obj) {
        JsonObject normalized = new JsonObject();
        for (String key : obj.keySet()) {
            normalized.add(key.toLowerCase(), obj.get(key));
        }
        return normalized;
    }

    protected void logRequest(String operation) {
        if( this.body == null || this.body.isEmpty()) {
            LogHelper.getInstance().logInfo("[rest:" + this.getAuthEntry().getClientId() + "] Request: " + operation + ", Path: " + this.context.path());
        } else {
            String processedBody = this.body.toString().replace("\"", "\\\"");

            if (processedBody.length() > MAX_BODY_LOG_OUTPUT) {
                processedBody = processedBody.substring(0, MAX_BODY_LOG_OUTPUT) + "...";
            }
            LogHelper.getInstance().logInfo("[rest:" + this.getAuthEntry().getClientId() + "] Request: " + operation + ", Path: " + this.context.path() +", Payload: " + processedBody);
        }
    }

    protected void logResponse(String result) {
        LogHelper.getInstance().logInfo("[rest:" + this.getAuthEntry().getClientId()+ "] Response: " + result);
    }

    protected void logResponse(String result, String responsePayLoad) {
        LogHelper.getInstance().logInfo("[rest:" + this.getAuthEntry().getClientId()+ "] Response: " + result + ", Response Payload: " + responsePayLoad);
    }

    protected void respondBadRequest(String message) {
        ApiResponse<Object> response = ApiResponse.error(message).withStatus(HttpCode.BAD_REQUEST.value());
        logResponse(response.getMessage());
        context.status(HttpCode.BAD_REQUEST.value()).json(response);
    }

    protected <T> void respondOk(ResultCode code, T data) {
        ApiResponse<T> response = ApiResponse.success(code, data);
        logResponse(response.getMessage(), (String) data);
        context.status(HttpCode.OK.value()).json(response);
    }

    protected <T> void respondOk(ResultCode code) {
        ApiResponse<T> response = (ApiResponse<T>) ApiResponse.success(code,null);
        logResponse(response.getMessage());
        context.status(HttpCode.OK.value()).json(response);
    }

    protected void respondBadRequest(ResultCode code) {
        ApiResponse<Object> response = ApiResponse.error(code).withStatus(HttpCode.BAD_REQUEST.value());
        logResponse(response.getMessage());
        context.status(HttpCode.BAD_REQUEST.value()).json(response);
    }

    protected void respondForbiddenAccess(ResultCode code) {
        ApiResponse<Object> response = ApiResponse.error(code).withStatus(HttpCode.FORBIDDEN.value());
        logResponse(response.getMessage());
        context.status(HttpCode.FORBIDDEN.value()).json(response);
    }

    protected void respondNotFound(ResultCode code) {
        ApiResponse<Object> response = ApiResponse.error(code).withStatus(HttpCode.NOT_FOUND.value());
        logResponse(response.getMessage());
        context.status(HttpCode.NOT_FOUND.value()).json(response);
    }

    protected boolean isReadOnly() {
        return authEntry.hasRestReadAccess();
    }

    protected boolean isWritable() { return authEntry.hasRestWriteAccess(); }

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
