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
package com.vertexcache.server.socket;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.security.EncryptionMode;
import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.module.model.Module;
import com.vertexcache.core.module.model.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.command.CommandService;
import com.vertexcache.core.status.SystemStatusReport;
import com.vertexcache.server.exception.VertexCacheSSLServerSocketException;

import javax.net.ssl.*;
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Core server module that listens for incoming socket connections and dispatches client handlers.
 *
 * Supports optional TLS transport encryption via ServerSecurityHelper.
 * Utilizes a fixed-size thread pool sized based on available processors to handle clients concurrently.
 *
 * Manages server socket lifecycle including startup, shutdown, and error handling.
 * Logs client connection details including transport type and message encryption status.
 *
 * Provides static accessors for startup status and messages for external monitoring.
 */
public class SocketServer extends Module {

    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private static final int QUEUE_SIZE = 1000;
    private static final int SOCKET_IDLE_TIMEOUT_MS = 30000;

    private ExecutorService executor;
    private ServerSocket serverSocket = null;

    private static ModuleStatus status = ModuleStatus.NOT_STARTED;
    private static String statusMessage = "";

    public SocketServer() {
    }

    @Override
    protected void onStart() {

        try {
            status = ModuleStatus.STARTUP_IN_PROGRESS;
            CommandService commandService = new CommandService();
            Cache.getInstance(Config.getInstance().getCacheConfigLoader().getCacheEvictionPolicy(), Config.getInstance().getCacheConfigLoader().getCacheSize());

            int port = Config.getInstance().getCoreConfigLoader().getServerPort();
            if(Config.getInstance().getClusterConfigLoader().isEnableClustering()) {
                // Is a Cluster Node, used the Cluster Node's respective port
                port = Config.getInstance().getClusterConfigLoader().getLocalClusterNode().getPortAsInt();
            } else {
                // Not a Cluster use the server_port
                port = Config.getInstance().getCoreConfigLoader().getServerPort();
            }

            ServerSocket serverSocket;
            if (Config.getInstance().getSecurityConfigLoader().isEncryptTransport()) {
                serverSocket = ServerSecurityHelper.createSecureSocket(port);
            } else {
                serverSocket = new ServerSocket(port);
            }

            status = ModuleStatus.STARTUP_SUCCESSFUL;
            outputStartup();

            this.executor = new ThreadPoolExecutor(
                    MAX_THREADS,
                    MAX_THREADS,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(QUEUE_SIZE),
                    new ThreadPoolExecutor.AbortPolicy()
            );

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(SOCKET_IDLE_TIMEOUT_MS);
                String address = clientSocket.getInetAddress().getHostAddress();
                port = clientSocket.getPort();
                String transport = (clientSocket instanceof SSLSocket) ? "TLS" : "Plain";
                String messageEncryption = Config.getInstance().getSecurityConfigLoader().getEncryptionMode() != EncryptionMode.NONE ? "Yes" : "No";
                outputInfo(transport + " client connected from " + address + ":" + port + " (Encrypted Messages: " + messageEncryption + ")");
                this.executor.execute(new ClientHandler(clientSocket, Config.getInstance(), commandService));
            }
        } catch (BindException e) {
            status = ModuleStatus.STARTUP_FAILED;
            statusMessage = "Error, Port already in used";
            outputStartup();
        } catch (IOException e) {
            status = ModuleStatus.STARTUP_FAILED;
            statusMessage = "FATAL, unexpected server error, please try again.";
            outputStartup();
        } catch (VertexCacheSSLServerSocketException e) {
            status = ModuleStatus.STARTUP_FAILED;
            statusMessage = e.getMessage();
            outputStartup();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch(Exception e) {
                }
            }
        }
    }

    public static ModuleStatus getStartupStatus() {
        return SocketServer.status;
    }

    public static String getStartupMessage() {
        return statusMessage;
    }

    private void outputStartup() {
        LogHelper.getInstance().logInfo(SystemStatusReport.getFullSystemReport());
    }

    private void outputInfo(String message) {
        if (Config.getInstance().getCoreConfigLoader().isEnableVerbose()) {
            LogHelper.getInstance().logInfo(message);
        }
    }

    @Override
    protected void onValidate() {
        if(status != ModuleStatus.NOT_STARTED) {
            LogHelper.getInstance().logInfo(SystemStatusReport.getStartupSystemReport());
            System.exit(0);
        }
    }

    @Override
    protected void onStop() {
        try {
            if (this.executor != null) {
                this.executor.shutdown();
            }
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (IOException exception) {
            LogHelper.getInstance().logError(exception.getMessage());
        }
    }

}
