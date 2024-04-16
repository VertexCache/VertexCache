package com.vertexcache;

import com.vertexcache.dashboard.VertexCacheDashboard;
import com.vertexcache.server.SocketServer;
import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.domain.config.Config;
import org.springframework.boot.SpringApplication;

public class VertexCacheServer {
    public static void main(String[] args) throws Exception {

        // Thread - VertexCache Socket Server
        Thread socketServerThread = new Thread(() -> {
            try {
                Config.getInstance().loadPropertiesFromArgs(new CommandLineArgsParser(args));
                SocketServer vertexCacheServer = new SocketServer();
                vertexCacheServer.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        socketServerThread.start();

        // Thread - Spring Boot Web Server - Dashboard
        Thread springBootApplicationThread = new Thread(() -> {
            SpringApplication.run(VertexCacheDashboard.class, args);
        });

        springBootApplicationThread.start();


        // Wait for both Threads to finish
        try {
            socketServerThread.join();
            springBootApplicationThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
