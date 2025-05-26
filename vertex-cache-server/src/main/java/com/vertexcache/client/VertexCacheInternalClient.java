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