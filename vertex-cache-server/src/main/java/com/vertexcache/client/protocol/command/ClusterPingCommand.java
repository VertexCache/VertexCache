package com.vertexcache.client.protocol.command;

import com.vertexcache.client.protocol.BaseCommand;
import com.vertexcache.common.log.LogHelper;

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

        try {

            System.out.println("responseBody: " + responseBody);

            if (responseBody == null || responseBody.isBlank() || responseBody.toLowerCase().contains("err")) {
                setFailure("Heartbeat failed: " + responseBody);
            }

        } catch (Exception ex) {

            LogHelper.getInstance().logFatal("==============> " + ex.getMessage());
        }
    }

    @Override
    protected String getCommandKey() {
        return "PEER_PING";
    }

    @Override
    public void onFailedConnect(String host, int port) {
        System.out.println("[ClusterPingCommand] Connection failed to " + host + ":" + port);
    }

    @Override
    public void onFailedSend(String command, Throwable cause) {
        System.out.println("[ClusterPingCommand] Failed to send command '" + command + "': " + cause.getMessage());
    }
}
