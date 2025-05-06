package com.vertexcache.core.command.impl.internal;

import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.server.session.ClientSessionContext;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.module.cluster.util.ClusterHashUtil;

import java.util.Map;
import java.util.Optional;

/**
 * example peer_ping node-b 2ec68a38a74c9744bbbdb8135f9d9719f6ccf8300c7b759ce99930ae108fb825
 */
public class PeerPingCommand extends BaseCommand<String> {

    public static final String COMMAND_KEY = "PEER_PING";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse commandResponse = new CommandResponse();

        if (argumentParser.getPrimaryArgument().getArgs().size() != 2) {
            commandResponse.setResponseError("ERR PEER_PING syntax: PEER_PING <nodeId> <configHash>");
            return commandResponse;
        }

        String nodeId = argumentParser.getPrimaryArgument().getArgs().getFirst();
        String remoteHash = argumentParser.getPrimaryArgument().getArgs().get(1);

        Optional<ClusterModule> optClusterModule = ModuleRegistry.getInstance().getModule(ClusterModule.class);
        if (optClusterModule.isEmpty()) {
            commandResponse.setResponseError("ERR Cluster module is not active");
            return commandResponse;
        }

        ClusterModule clusterModule = optClusterModule.get();

        if (!Config.getInstance().getClusterConfigLoader().getAllClusterNodes().containsKey(nodeId)) {
            commandResponse.setResponseError("ERR Unknown node ID: " + nodeId);
            return commandResponse;
        }

        clusterModule.getClusterNodeTrackerStore().updateHeartbeat(nodeId);

        Map<String, String> localSettings = Config.getInstance().getClusterConfigLoader().getCoordinationSettings();
        String localHash = ClusterHashUtil.computeCoordinationHash(localSettings);

        if (!localHash.equals(remoteHash)) {
            LogHelper.getInstance().logWarn("[PEER_PING] Config hash mismatch from " + nodeId + ". Local: " + localHash + ", Remote: " + remoteHash);
            commandResponse.setResponse("WARN Hash mismatch. Local: " + localHash);
        } else {
            LogHelper.getInstance().logDebug("[PEER_PING] Hash OK from " + nodeId);
            commandResponse.setResponseOK();
        }

        return commandResponse;
    }
}

