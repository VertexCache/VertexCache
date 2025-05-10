package com.vertexcache.client.protocol.command;

import com.vertexcache.client.protocol.BaseCommand;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.module.cluster.ClusterModule;

import java.util.Optional;

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
