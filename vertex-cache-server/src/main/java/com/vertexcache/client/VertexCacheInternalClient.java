package com.vertexcache.client;

import com.vertexcache.client.protocol.command.ClusterPingCommand;
import com.vertexcache.client.result.CommandResult;
import com.vertexcache.client.transport.TcpClient;

/**
 * NOTE: This is NOT the JAVA SDK, although alot of the code is directly borrowed from that
 *
 * This client is used internally to re-use the Socket TCP connection to communicate between
 * Nodes ie: Clustering
 */
public class VertexCacheInternalClient {

    private final VertexCacheInternalClientOptions vertexCacheSDKOptions;
    private final TcpClient tcpClient;

    public VertexCacheInternalClient(VertexCacheInternalClientOptions options) {
        this.vertexCacheSDKOptions = options;

        this.tcpClient = new TcpClient(
                options.getServerHost(),
                options.getServerPort(),
                options.isEnableTlsEncryption(),
                options.isVerifyCertificate(),
                options.getTlsCertificate(),
                options.getConnectTimeout(),
                options.getReadTimeout(),
                options.getEncryptionMode(),
                options.getPublicKey(),
                options.getSharedEncryptionKey(),
                options.getClientId(),
                options.getClientToken()
        );
    }

    public CommandResult sendClusterPingCommand(String nodeId, String configHash) {
        ClusterPingCommand cmd = (ClusterPingCommand) new ClusterPingCommand(nodeId, configHash).execute(tcpClient);
        return new CommandResult(cmd.isSuccess(), cmd.getStatusMessage());
    }

    public boolean isConnected() {
        return tcpClient.isConnected();
    }

    public void close() {
        tcpClient.close();
    }
}