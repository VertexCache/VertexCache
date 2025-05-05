package com.vertexcache.core.command.impl;

import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.server.session.ClientSessionContext;

import java.util.Optional;

public class ClusterStatusCommand extends BaseCommand<String> {

    public static final String COMMAND_KEY = "CLUSTER_INFO";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) throws Exception {
        CommandResponse commandResponse = new CommandResponse();

        if(!Config.getInstance().getClusterConfigLoader().isEnableClustering()) {
            commandResponse.setResponseError("Clustering disabled, no cluster info to return");

        } else {

            Optional<ClusterModule> optionalClusterModule = ModuleRegistry.getInstance().getModule(ClusterModule.class);

            if (optionalClusterModule.isEmpty()) {
                commandResponse.setResponseError("Cluster module is not loaded or enabled.");
                return commandResponse;
            }

            ClusterModule clusterModule = optionalClusterModule.get();

            //ClusterState clusterState = clusterModule.getClusterState();
            //ClusterPeerStore peerStore = clusterModule.getPeerStore();

            commandResponse.setResponse("Dummy response");

        }

        return commandResponse;
    }
}
