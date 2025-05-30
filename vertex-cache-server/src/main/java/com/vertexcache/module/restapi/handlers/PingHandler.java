/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.module.restapi.handlers;

import com.vertexcache.core.util.message.ResultCode;
import com.vertexcache.module.restapi.model.HttpCode;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import com.vertexcache.module.restapi.model.ApiResponse;

/**
 * Simple REST handler for health checks and liveness probes.
 *
 * Responds with HTTP 200 and a "PONG" result code to indicate the service is running.
 */
public class PingHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        ctx.status(HttpCode.OK.value()).json( ApiResponse.success(ResultCode.PONG, null));
    }
}
