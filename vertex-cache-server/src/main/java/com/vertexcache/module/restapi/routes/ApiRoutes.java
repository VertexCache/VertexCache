package com.vertexcache.module.restapi.routes;

//import com.vertexcache.module.restapi.handlers.GetHandler;
//import com.vertexcache.module.restapi.handlers.SetHandler;
//import com.vertexcache.module.restapi.handlers.DeleteHandler;
import com.vertexcache.module.restapi.handlers.SetHandler;
import com.vertexcache.module.restapi.handlers.StatusHandler;
import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class ApiRoutes {

    public static void register(Javalin app) {
        app.get("/status", new StatusHandler());
       // app.get("/get", new GetHandler());
      //  app.post("/set", new SetHandler());
      //  app.post("/del", new DeleteHandler());

        app.post("/cache", new SetHandler());
    }
}
