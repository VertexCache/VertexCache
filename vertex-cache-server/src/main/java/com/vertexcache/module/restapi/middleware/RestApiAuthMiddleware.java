package com.vertexcache.module.restapi.middleware;

import com.vertexcache.core.setting.Config;
import com.vertexcache.module.auth.model.AuthEntry;
import com.vertexcache.module.auth.service.AuthService;
import com.vertexcache.module.restapi.model.RestApiContextKeys;
import com.vertexcache.common.log.LogHelper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;

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

        AuthEntry client = AuthService.getInstance().getClientByToken(token);
        if (client == null) {
            LogHelper.getInstance().logWarn("[REST API] Invalid token on path: " + path);
            throw new UnauthorizedResponse("Invalid auth token");
        }

        // Inject into context
        ctx.attribute(RestApiContextKeys.AUTH_ENTRY, client);
    }
}
