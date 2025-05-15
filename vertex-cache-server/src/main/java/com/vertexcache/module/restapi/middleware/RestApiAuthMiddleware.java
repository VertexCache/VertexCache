package com.vertexcache.module.restapi.middleware;

import com.vertexcache.core.setting.Config;

import com.vertexcache.module.auth.model.AuthEntry;
import com.vertexcache.module.auth.service.AuthService;
import com.vertexcache.module.restapi.util.RestApiContextKeys;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class RestApiAuthMiddleware implements Handler {

    @Override
    public void handle(Context ctx) {
        var config = Config.getInstance().getRestApiConfigLoader();
        String headerName = config.getTokenHeader().name().replace("_", "-");
        String token = ctx.header(headerName);

        if (token == null || token.isBlank()) {
            ctx.status(401).result("Missing auth token");
            return;
        }

        AuthEntry client = AuthService.getInstance().getClientByToken(token);
        if (client == null) {
            ctx.status(401).result("Invalid auth token");
            return;
        }

        ctx.attribute(RestApiContextKeys.AUTH_ENTRY, client);
    }
}
