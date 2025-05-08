package com.vertexcache.client.protocol.command;

import com.vertexcache.client.protocol.BaseCommand;

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
}
