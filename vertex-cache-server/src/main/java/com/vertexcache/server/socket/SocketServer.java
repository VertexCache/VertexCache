package com.vertexcache.server.socket;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.protocol.EncryptionMode;
import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.command.CommandService;
import com.vertexcache.core.status.SystemStatusReport;

import javax.net.ssl.*;
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

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
            //Cache.getInstance(Config.getInstance().getCacheEvictionPolicy(), Config.getInstance().getCacheSize());
            Cache.getInstance(Config.getInstance().getConfigCache().getCacheEvictionPolicy(), Config.getInstance().getConfigCache().getCacheSize());

            ServerSocket serverSocket;
            if (Config.getInstance().getConfigSecurity().isEncryptTransport()) {
                serverSocket = ServerSecurityHelper.createSecureSocket();
            } else {
                serverSocket = new ServerSocket(Config.getInstance().getServerPort());
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
                int port = clientSocket.getPort();
                String transport = (clientSocket instanceof SSLSocket) ? "TLS" : "Plain";
                String messageEncryption = Config.getInstance().getConfigSecurity().getEncryptionMode() != EncryptionMode.NONE ? "Yes" : "No";
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
        if (Config.getInstance().isEnableVerbose()) {
            LogHelper.getInstance().logInfo(message);
        }
    }

    @Override
    protected void onValidate() {
        //status = ModuleStatus.STARTUP_FAILED;
        //statusMessage = "IT FAILEd";
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
