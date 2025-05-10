package com.vertexcache.core.command.impl.internal;

import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.core.validation.validators.cluster.ClusterNodeRoleValidator;
import com.vertexcache.core.validation.validators.cluster.NodeIdExistsValidator;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.server.session.ClientSessionContext;

import java.util.Optional;

public class RoleChangeCommand extends BaseCommand<String> {

    public static final String COMMAND_KEY = "ROLE_CHANGE";
    private ClusterModule clusterModule;

    public RoleChangeCommand() {


    }

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse commandResponse = new CommandResponse();

        if (argumentParser.getPrimaryArgument().getArgs().size() != 2) {
            commandResponse.setResponseError("ROLE_CHANGE syntax: ROLE_CHANGE <nodeId> <newRole>");
            return commandResponse;
        }

        Optional<ClusterModule> optionalClusterModule = ModuleRegistry.getInstance().getModule(ClusterModule.class);

        if (optionalClusterModule.isEmpty()) {
            commandResponse.setResponseError("Cluster module is not loaded or enabled.");
            return commandResponse;
        }

        this.clusterModule = optionalClusterModule.get();

        String nodeId = argumentParser.getPrimaryArgument().getArgs().getFirst();
        String newRole = argumentParser.getPrimaryArgument().getArgs().get(1);

        // Validate node exists
        try {
            new NodeIdExistsValidator(nodeId).validate();
            new ClusterNodeRoleValidator(newRole).validate();
        } catch (VertexCacheValidationException e) {
            commandResponse.setResponseError(e.getMessage());
            return commandResponse;
        }
        //Validate role is PRIMARY or SECONDARY
        try {
            new ClusterNodeRoleValidator(newRole).validate();
        } catch (VertexCacheValidationException e) {
            commandResponse.setResponseError(e.getMessage());
            return commandResponse;
        }

        //clusterModule.getClusterNodeTrackerStore().notifyRoleChange(nodeId, newRole);

        commandResponse.setResponseOK();
        return commandResponse;
    }
}
