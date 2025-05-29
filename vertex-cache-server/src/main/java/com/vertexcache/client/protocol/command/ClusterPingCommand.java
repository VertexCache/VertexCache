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
package com.vertexcache.client.protocol.command;

import com.vertexcache.client.protocol.BaseCommand;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.module.cluster.ClusterModule;

import java.util.Optional;

/**
 * Command used by standby-enabled cluster nodes to perform a "PING" check
 * and verify whether a peer node is responsive.
 *
 * Note: This command is not issued by the primary node; it is only used by
 * standby nodes for liveness detection.
 */
public class ClusterPingCommand extends BaseCommand<ClusterPingCommand> {

    private final String nodeId;
    private final String configHash;

    public ClusterPingCommand(String nodeId, String configHash) {
        this.nodeId = nodeId;
        this.configHash = configHash;
    }

    @Override
    protected String buildCommand() {
        return "PEER_PING " + nodeId + " " + configHash;
    }

    @Override
    protected void parseResponse(String responseBody) {
        if (responseBody == null || responseBody.isBlank() || responseBody.toLowerCase().contains("err")) {
            setFailure("Heartbeat failed: " + responseBody);
        }
    }

    @Override
    protected String getCommandKey() {
        return "PEER_PING";
    }

    @Override
    public void onFailedConnect(String host, int port) {
        LogHelper.getInstance().logWarn("[ClusterPingCommand] Connection failed to " + host + ":" + port);

        Optional<ClusterModule> optClusterModule = ModuleRegistry.getInstance().getModule(ClusterModule.class);
        if (optClusterModule.isPresent()) {
            ClusterModule clusterModule = optClusterModule.get();
            if (clusterModule.getClusterConfig().isSecondaryNode()) {
                clusterModule.getClusterConfig().getPrimaryEnabledClusterNode().getHeartbeat().markDown();
            }
        } else {
            LogHelper.getInstance().logWarn("[ClusterPingCommand] Cluster module not available.");
        }
    }

    @Override
    public void onFailedSend(String command, Throwable cause) {
        // No-op
        //System.out.println("[ClusterPingCommand] Failed to send command '" + command + "': " + cause.getMessage());
    }
}
