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
package com.vertexcache.module.restapi.middleware;

import com.vertexcache.core.setting.Config;
import com.vertexcache.module.auth.model.AuthEntry;
import com.vertexcache.module.auth.service.AuthService;
import com.vertexcache.module.restapi.model.RestApiContextKeys;
import com.vertexcache.common.log.LogHelper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;

/**
 * Middleware handler for REST API authentication.
 *
 * Extracts a bearer token from the configured header, validates it,
 * and authenticates the client via AuthService.
 * On success, injects the authenticated client info into the request context.
 * Throws UnauthorizedResponse on missing or invalid tokens.
 */
public class RestApiAuthMiddleware implements Handler {

    @Override
    public void handle(Context ctx) {
        String path = ctx.path(); // optional logging aid
        var config = Config.getInstance().getRestApiConfigLoader();
        String headerName = config.getTokenHeader().name().replace("_", "-");

        String token = ctx.header(headerName);
        if (token != null && token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || token.isBlank()) {
            throw new UnauthorizedResponse("Missing auth token");
        }

        AuthEntry client = AuthService.getInstance().authenticateByToken(token);
        if (client == null) {
            LogHelper.getInstance().logWarn("[REST API] Invalid token on path: " + path);
            throw new UnauthorizedResponse("Invalid auth token");
        }

        // Inject into context
        ctx.attribute(RestApiContextKeys.AUTH_ENTRY, client);
    }
}
