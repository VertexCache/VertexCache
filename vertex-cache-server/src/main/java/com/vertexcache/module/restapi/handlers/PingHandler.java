package com.vertexcache.module.restapi.handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import com.vertexcache.module.restapi.model.ApiResponse;

public class PingHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        ctx.status(200).json(ApiResponse.success("PONG"));
    }
}
