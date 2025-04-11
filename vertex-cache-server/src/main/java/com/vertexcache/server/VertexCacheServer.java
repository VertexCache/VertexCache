package com.vertexcache.server;

import com.vertexcache.server.socket.SocketServer;
import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.module.ModuleRegistry;

public class VertexCacheServer {
    public static void main(String[] args) throws Exception {
        Config.getInstance().loadPropertiesFromArgs(new CommandLineArgsParser(args));

        // Load all enabled modules
        ModuleRegistry moduleRegistry = new ModuleRegistry();
        moduleRegistry.loadModules();


        // Start the socket server
        SocketServer vertexCacheServer = new SocketServer();
        vertexCacheServer.execute();

        // Shutdown hook to stop modules gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(moduleRegistry::stopModules));
    }
}
