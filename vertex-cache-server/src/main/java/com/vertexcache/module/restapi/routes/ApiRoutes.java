package com.vertexcache.module.restapi.routes;

import com.vertexcache.module.restapi.handlers.*;
import com.vertexcache.module.restapi.server.RestApiServer;
import io.javalin.Javalin;

public class ApiRoutes {

    public static void register(Javalin app) {
        app.get(RestApiServer.API_PREFIX + "/ping", new PingHandler());

        app.post(RestApiServer.API_PREFIX + "/cache", new SetHandler());
        app.get(RestApiServer.API_PREFIX + "/cache/primary/{key}", new GetHandler());
        app.get(RestApiServer.API_PREFIX + "/cache/idx1/{idx1}", new GetIdx1Handler());
        app.get(RestApiServer.API_PREFIX + "/cache/idx2/{idx2}", new GetIdx2Handler());
        app.delete(RestApiServer.API_PREFIX + "/cache/primary/{key}", new DelHandler());

    }
}
