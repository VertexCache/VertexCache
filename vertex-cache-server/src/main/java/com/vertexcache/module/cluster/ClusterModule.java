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
package com.vertexcache.module.cluster;

import com.vertexcache.client.VertexCacheInternalClient;
import com.vertexcache.client.VertexCacheInternalClientOptions;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.security.EncryptionMode;
import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.setting.loader.ClusterConfigLoader;
import com.vertexcache.core.validation.validators.cluster.*;
import com.vertexcache.module.alert.AlertModule;
import com.vertexcache.module.alert.AlertModuleNoOp;
import com.vertexcache.module.alert.listeners.ClusterNodeEventListener;
import com.vertexcache.module.cluster.exception.VertexCacheClusterModuleException;
import com.vertexcache.module.cluster.model.ClusterNodeRole;
import com.vertexcache.module.cluster.service.coordination.FailoverManager;
import com.vertexcache.module.cluster.service.heartbeat.HeartbeatManager;
import com.vertexcache.module.cluster.model.ClusterNode;
import com.vertexcache.module.cluster.util.ClusterHashUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClusterModule extends Module {

    private ClusterConfigLoader clusterConfig;
    private HeartbeatManager heartbeatManager;
    private FailoverManager failoverManager;
    private VertexCacheInternalClient vertexCacheInternalClient = null;
    private String localRoleOverride = null;
    private ClusterNode localNode;
    private ClusterNodeEventListener clusterNodeEventListener;

    @Override
    protected void onStart() {
        try {
            this.clusterConfig = Config.getInstance().getClusterConfigLoader();
            this.localNode = clusterConfig.getAllClusterNodes().get(Config.getInstance().getClusterConfigLoader().getLocalNodeId());

            // If Secondary Enabled Node
            if(Config.getInstance().getClusterConfigLoader().isSecondaryNode()) {

                // Initialize internal client for intra-cluster messaging
                this.initVertexCacheClient();

                // Start heartbeat manager
                this.heartbeatManager = new HeartbeatManager(this, this.clusterConfig.getClusterHeartbeatIntervalMs());
                this.heartbeatManager.start();

                // Start failover manager
                this.failoverManager = new FailoverManager(this);
            }

            if(this.getModuleStatus() == ModuleStatus.NOT_STARTED) {


                Optional<AlertModule> optionalAlertModule = ModuleRegistry.getInstance().getModule(AlertModule.class);

                if (!Config.getInstance().getAlertConfigLoader().isEnableAlerting()) {
                    LogHelper.getInstance().logInfo("[Alert] AlertModule not enabled — skipping alert wiring.");
                    this.clusterNodeEventListener = new AlertModuleNoOp(); // or skip wiring altogether
                } else {
                    this.clusterNodeEventListener = optionalAlertModule.get();
                }


                reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Cluster module started successfully");
            } else {
                this.heartbeatManager.shutdown();
            }
        } catch (Exception e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, "Exception during cluster initialization: " + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        if (heartbeatManager != null) {
            heartbeatManager.shutdown(); // Cleanly stop scheduled heartbeat thread
        }
        this.clusterConfig = null;
        setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

    @Override
    protected void onValidate() {
        try {
            ClusterConfigLoader clusterConfig = Config.getInstance().getClusterConfigLoader();

            if (clusterConfig == null) {
                reportHealth(ModuleStatus.STARTUP_FAILED, "Cluster config loader not initialized.");
                throw new VertexCacheClusterModuleException("Cluster config loader is null.");
            }

            Map<String, ClusterNode> nodes = clusterConfig.getAllClusterNodes();
            if (nodes.isEmpty()) {
                reportHealth(ModuleStatus.STARTUP_FAILED, "No cluster nodes defined in configuration.");
                throw new VertexCacheClusterModuleException("Cluster node list is empty.");
            }

            new ClusterTopologyValidator(nodes).validate();

            Map<String, String> settings = clusterConfig.getCoordinationSettings();
            new ClusterCoordinationSettingsValidator(settings).validate();

        } catch (Exception e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, "Cluster validation failed due invalid configuration");
        }
    }

    private void initVertexCacheClient() {
        if(this.vertexCacheInternalClient == null) {

            ClusterNode clusterNode = null;
            if(Config.getInstance().getClusterConfigLoader().isPrimaryNode()) {
                 clusterNode = Config.getInstance().getClusterConfigLoader().getSecondaryEnabledClusterNode();
            }

            if(Config.getInstance().getClusterConfigLoader().isSecondaryNode()) {
                clusterNode = Config.getInstance().getClusterConfigLoader().getPrimaryEnabledClusterNode();
            }

            if(clusterNode != null) {
                VertexCacheInternalClientOptions options = new VertexCacheInternalClientOptions();
                options.setClientId(clusterNode.getId());

                // Node to Node - token not required
                options.setClientToken("");
                options.setServerHost(clusterNode.getHost());
                options.setServerPort(Integer.parseInt(clusterNode.getPort()));

                // TLS
                if (Config.getInstance().getSecurityConfigLoader().isEncryptTransport()) {
                    options.setEnableTlsEncryption(true);
                    options.setTlsCertificate(Config.getInstance().getSecurityConfigLoader().getTlsCertificate());
                } else {
                    options.setEnableTlsEncryption(false);
                }

                // Message Layer Encryption
                if (Config.getInstance().getSecurityConfigLoader().getEncryptionMode().equals(EncryptionMode.ASYMMETRIC)) {
                    options.setEncryptionMode(EncryptionMode.ASYMMETRIC);
                    options.setPublicKey(Config.getInstance().getSecurityConfigLoader().getPublicKey());
                } else if (Config.getInstance().getSecurityConfigLoader().getEncryptionMode().equals(EncryptionMode.SYMMETRIC)) {
                    options.setEncryptionMode(EncryptionMode.SYMMETRIC);
                    options.setSharedEncryptionKey(Config.getInstance().getSecurityConfigLoader().getSharedEncryptionKey());
                } else {
                    options.setEncryptionMode(EncryptionMode.NONE);
                }
                options.setEncryptionMode(EncryptionMode.ASYMMETRIC);
                this.vertexCacheInternalClient = new VertexCacheInternalClient(options);

                if(!this.vertexCacheInternalClient.isConnected()) {
                    reportHealth(ModuleStatus.STARTUP_FAILED, "Cluster, secondary node failed to connect with Primary, check if Primary node is up.");
                }

            } else {
                reportHealth(ModuleStatus.STARTUP_FAILED, "Cluster Nodes configuration failed for internal client");
            }
        }
    }

    public ClusterConfigLoader getClusterConfig() {
        return clusterConfig;
    }

    public ClusterNode getLocalNode() {
        return localNode;
    }

    public List<ClusterNode> getPeers() {
        return clusterConfig.getAllClusterNodes()
                .values()
                .stream()
                .filter(node -> !node.getId().equals(localNode.getId()))
                .toList();
    }

    public ClusterNode getPrimaryNode() {
        return clusterConfig.getPrimaryEnabledClusterNode();
    }

    public void promoteSelfToPrimary() {
        if (!ClusterNodeRole.SECONDARY.equals(localNode.getRole())) {
            // Shouldn't happen
            LogHelper.getInstance().logWarn("Local node is not " + ClusterNodeRole.SECONDARY.name() + " — cannot promote.");
            return;
        }

        LogHelper.getInstance().logInfo("[ClusterModule] Promoting " + ClusterNodeRole.SECONDARY.name() + " node '" + localNode.getId() + "' to " + ClusterNodeRole.PRIMARY.name() + ".");
        Config.getInstance().getClusterConfigLoader().getSecondaryEnabledClusterNode().setPromotedToPrimary(true);

        // Secondary is now Primary, deactivate the heartbeat
        this.heartbeatManager.shutdown();

        new Thread(() -> {
            try {
                // Start RestApiModule (assuming the config has it enabled)
                ModuleRegistry.startRestApiModule();

                // Start SmartModule (assuming the config has it enabled and at least one service is enabled)
                ModuleRegistry.startSmartModule();

                reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, localNode.getId() + " promoted to PRIMARY.");
                clusterNodeEventListener.onSecondaryNodePromotedToPrimary(localNode.getId());
            } catch (Exception ex) {
                LogHelper.getInstance().logError("Promotion thread failed");
            }
        }, "PromotionStartupThread").start();
    }

    public void clusterPing(ClusterNode node) {
        try {
            String hash = ClusterHashUtil.computeCoordinationHash(clusterConfig.getCoordinationSettings());
           vertexCacheInternalClient.sendClusterPingCommand(localNode.getId(),hash);
        } catch (Exception e) {
            clusterConfig.getPrimaryEnabledClusterNode().getHeartbeat().markDown();
            LogHelper.getInstance().logError("Failed to send heartbeat to " + ClusterNodeRole.PRIMARY.name() + " node '" + node.getId() + "': " + e.getMessage());
        }
    }
}
