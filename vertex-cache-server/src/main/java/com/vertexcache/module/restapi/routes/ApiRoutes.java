package com.vertexcache.module.restapi.routes;

import com.vertexcache.module.restapi.handlers.*;
import io.javalin.Javalin;

public class ApiRoutes {

    public static void register(Javalin app) {
        app.get("/ping", new PingHandler());

        app.post("/cache", new SetHandler());
        app.get("/cache/by-primary/{key}", new GetHandler());
        //app.get("/cache/by-secondary/:idx1", new GetIdx1Handler());
        //app.get("/cache/by-tertiary/:idx2", new GetIdx2Handler());

        //app.delete("/cache/:key", new DelHandler());

    }
}
