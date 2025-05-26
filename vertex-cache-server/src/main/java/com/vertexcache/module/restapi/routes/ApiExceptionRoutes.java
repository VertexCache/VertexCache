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
package com.vertexcache.module.restapi.routes;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.module.restapi.model.ApiResponse;
import io.javalin.Javalin;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

public class ApiExceptionRoutes {

    public static void register(Javalin app) {
        app.exception(UnauthorizedResponse.class, (e, ctx) -> {
            ctx.status(401).json(ApiResponse.error(e.getMessage()));
        });

        app.exception(ForbiddenResponse.class, (e, ctx) -> {
            ctx.status(403).json(ApiResponse.error(e.getMessage()));
        });

        // Optional: catch-all fallback
        app.exception(Exception.class, (e, ctx) -> {
            LogHelper.getInstance().logFatal("Unhandled exception at " + ctx.path(), e);
            ctx.status(500).json(ApiResponse.error("Internal server error"));
        });
    }
}
