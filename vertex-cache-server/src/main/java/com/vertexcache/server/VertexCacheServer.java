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
package com.vertexcache.server;

import com.vertexcache.server.socket.SocketServer;
import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.module.ModuleRegistry;

public class VertexCacheServer {
    public static void main(String[] args) throws Exception {

        // TODO - Modify so this loadable to support Embeddable Java use
        Config.getInstance().loadPropertiesFromArgs(new CommandLineArgsParser(args));

        // Load all enabled modules
        ModuleRegistry.getInstance().loadModules();

        // Start the socket server
        SocketServer vertexCacheServer = new SocketServer();
        vertexCacheServer.start();

        // Shutdown hook to stop modules gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                ModuleRegistry.getInstance().stopModules()
        ));
    }
}
