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
