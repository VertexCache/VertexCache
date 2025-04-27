package com.vertexcache.module.cluster;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.setting.loader.ClusterConfigLoader;
import com.vertexcache.core.validation.ValidationBatch;
import com.vertexcache.core.validation.validators.cluster.ClusterNodeHostValidator;
import com.vertexcache.core.validation.validators.cluster.ClusterNodePortValidator;
import com.vertexcache.core.validation.validators.cluster.ClusterNodeRoleValidator;
import com.vertexcache.core.validation.validators.cluster.ClusterNodeStatusValidator;
import com.vertexcache.module.cluster.enums.ClusterNodeRole;
import com.vertexcache.module.cluster.exception.VertexCacheClusterModuleException;
import com.vertexcache.module.cluster.heartbeat.HeartbeatManager;
import com.vertexcache.module.cluster.model.ClusterNode;
import com.vertexcache.module.cluster.store.ClusterPeerStore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClusterModule extends Module {

    private ClusterConfigLoader clusterConfig;
    private ClusterPeerStore peerStore = new ClusterPeerStore();
    private HeartbeatManager heartbeatManager;

    @Override
    protected void onStart() {
        try {
            this.clusterConfig = Config.getInstance().getClusterConfigLoader();
            this.heartbeatManager = new HeartbeatManager(this, getHeartbeatIntervalMs());
            new Thread(heartbeatManager, "ClusterHeartbeatThread").start();

            reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Cluster nodes loaded successfully");
        } catch (Exception e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, "Exception during cluster initialization: " + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        if (heartbeatManager != null) {
            heartbeatManager.stop();
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

            var nodes = clusterConfig.getAllClusterNodes();
            if (nodes.isEmpty()) {
                reportHealth(ModuleStatus.STARTUP_FAILED, "No cluster nodes defined in configuration.");
                throw new VertexCacheClusterModuleException("Cluster node list is empty.");
            }

            ValidationBatch batch = new ValidationBatch();
            int primaryCount = 0;
            int secondaryCount = 0;
            Set<String> endpoints = new HashSet<>();

            for (var node : nodes.values()) {
                String nodeRef = "node[" + node.id() + "]";
                batch.check(nodeRef + ".role", new ClusterNodeRoleValidator(node.role()));
                batch.check(nodeRef + ".status", new ClusterNodeStatusValidator(node.status()));
                batch.check(nodeRef + ".host", new ClusterNodeHostValidator(node.host()));
                batch.check(nodeRef + ".port", new ClusterNodePortValidator(node.port()));

                try {
                    ClusterNodeRole roleEnum = ClusterNodeRole.from(node.role());
                    switch (roleEnum) {
                        case PRIMARY -> primaryCount++;
                        case SECONDARY -> secondaryCount++;
                    }
                } catch (IllegalArgumentException ignored) {
                    // Already handled by role validator.
                }

                String endpoint = node.host() + ":" + node.port();
                if (!endpoints.add(endpoint)) {
                    batch.getErrors().add("Topology: Duplicate host:port detected for " + endpoint);
                }
            }

            if (primaryCount != 1) {
                batch.getErrors().add("Topology: Exactly 1 PRIMARY node required, found: " + primaryCount);
            }

            if (secondaryCount < 1) {
                batch.getErrors().add("Topology: At least 1 SECONDARY node required, found: " + secondaryCount);
            }

            if (batch.hasErrors()) {
                String summary = batch.getSummary();
                reportHealth(ModuleStatus.STARTUP_FAILED, "Cluster validation failed: " + summary);
                throw new VertexCacheClusterModuleException("Cluster validation failed: " + summary);
            }

            reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Cluster nodes validated successfully.");

        } catch (Exception e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, "Cluster validation failed: " + e.getMessage());
            throw e;
        }
    }

    public ClusterConfigLoader getClusterConfig() {
        return clusterConfig;
    }

    public ClusterPeerStore getPeerStore() {
        return peerStore;
    }

    public void pingPeer(ClusterNode peer) {
        try {
            // Placeholder for actual network call / heartbeat logic
            LogHelper.getInstance().logDebug("Pinging peer: " + peer.id());
            // Simulate success:
            peerStore.updateHeartbeat(peer.id());
        } catch (Exception e) {
            peerStore.markPeerDown(peer.id());
            LogHelper.getInstance().logError("Failed to ping peer '" + peer.id() + "': " + e.getMessage());
        }
    }

    public int getHeartbeatIntervalMs() {
        return Integer.parseInt(clusterConfig.getCoordinationSettings().getOrDefault("cluster_failover_check_interval_ms", "2000"));
    }

    public ClusterNode getLocalNode() {
        return Config.getInstance().getClusterConfigLoader().getAllClusterNodes()
                .get(Config.getInstance().getClusterConfigLoader().getLocalNodeId());
    }

    public List<ClusterNode> getPeers() {
        return Config.getInstance().getClusterConfigLoader().getAllClusterNodes()
                .values()
                .stream()
                .filter(node -> !node.id().equals(Config.getInstance().getClusterConfigLoader().getLocalNodeId()))
                .collect(Collectors.toList());
    }

    public ClusterNode getPrimaryNode() {
        return Config.getInstance().getClusterConfigLoader().getAllClusterNodes()
                .values()
                .stream()
                .filter(node -> "PRIMARY".equalsIgnoreCase(node.role()))
                .findFirst()
                .orElse(null);
    }

    public List<ClusterNode> getSecondaryNodes() {
        return Config.getInstance().getClusterConfigLoader().getAllClusterNodes()
                .values()
                .stream()
                .filter(node -> "SECONDARY".equalsIgnoreCase(node.role()))
                .collect(Collectors.toList());
    }

    public boolean isPrimary() {
        return "PRIMARY".equalsIgnoreCase(getLocalNode().role());
    }

    public boolean isSecondary() {
        return "SECONDARY".equalsIgnoreCase(getLocalNode().role());
    }
}
