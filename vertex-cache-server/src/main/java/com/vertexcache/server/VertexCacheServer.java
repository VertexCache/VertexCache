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
