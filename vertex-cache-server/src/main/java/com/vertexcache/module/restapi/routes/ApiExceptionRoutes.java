package com.vertexcache.module.restapi.routes;

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
            ctx.status(500).json(ApiResponse.error("Internal server error"));
        });
    }
}
