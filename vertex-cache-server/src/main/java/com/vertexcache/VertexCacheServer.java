package com.vertexcache;

import com.vertexcache.server.SocketServer;
import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.domain.config.Config;

public class VertexCacheServer {
    public static void main(String[] args) throws Exception {
        Config.getInstance().loadPropertiesFromArgs(new CommandLineArgsParser(args));
        SocketServer vertexCacheServer = new SocketServer();
        vertexCacheServer.execute();
    }
}
