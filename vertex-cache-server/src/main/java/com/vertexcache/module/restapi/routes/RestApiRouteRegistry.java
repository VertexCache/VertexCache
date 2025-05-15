package com.vertexcache.module.restapi.routes;

//import com.vertexcache.module.restapi.handlers.GetHandler;
//import com.vertexcache.module.restapi.handlers.SetHandler;
//import com.vertexcache.module.restapi.handlers.DeleteHandler;
import io.javalin.Javalin;

public class RestApiRouteRegistry {

    public static void register(Javalin app) {
        app.get("/status", ctx -> ctx.result("VertexCache REST API running"));
       // app.get("/get", new GetHandler());
      //  app.post("/set", new SetHandler());
      //  app.post("/del", new DeleteHandler());
    }
}
