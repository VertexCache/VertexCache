package com.vertexcache.module.restapi.handlers;

import com.vertexcache.core.util.message.ResultCode;
import com.vertexcache.module.restapi.model.HttpCode;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import com.vertexcache.module.restapi.model.ApiResponse;

public class PingHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        ctx.status(HttpCode.OK.value()).json( ApiResponse.success(ResultCode.PONG, null));
    }
}
