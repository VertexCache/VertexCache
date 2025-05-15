package com.vertexcache.module.restapi;

import com.vertexcache.module.restapi.middleware.RestApiAuthMiddleware;
import io.javalin.Javalin;

public class RestApiServer {

    private Javalin app;

    public void start(int port) {
        this.app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        });

        // Middleware: Auth checks before any route
        app.before(new RestApiAuthMiddleware());

        // Simple status route
        app.get("/status", ctx -> {
            ctx.result("VertexCache REST API is running");
        });

        app.start(port);
    }

    public void stop() {
        if (app != null) {
            app.stop();
        }
    }
}
