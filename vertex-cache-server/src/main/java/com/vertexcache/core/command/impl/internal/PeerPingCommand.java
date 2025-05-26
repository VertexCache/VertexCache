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

        LogHelper.getInstance().logInfo("[PeerPingCommand] Received PEER_PING from " + nodeId + " with hash: " + remoteHash);

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

