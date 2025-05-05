package com.vertexcache.module.cluster;

import com.vertexcache.client.VertexCacheInternalClient;
import com.vertexcache.client.VertexCacheInternalClientOptions;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.security.EncryptionMode;
import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.setting.loader.ClusterConfigLoader;
import com.vertexcache.core.validation.validators.cluster.*;
import com.vertexcache.module.cluster.exception.VertexCacheClusterModuleException;
import com.vertexcache.module.cluster.model.ClusterNodeRole;
import com.vertexcache.module.cluster.service.ClusterNodeLoggerObserver;
import com.vertexcache.module.cluster.service.coordination.FailoverManager;
import com.vertexcache.module.cluster.service.heartbeat.HeartbeatManager;
import com.vertexcache.module.cluster.model.ClusterNode;
import com.vertexcache.module.cluster.service.ClusterNodeTrackerStore;

import java.util.List;
import java.util.Map;

public class ClusterModule extends Module {

    private ClusterConfigLoader clusterConfig;
    private final ClusterNodeTrackerStore clusterNodeTrackerStore = new ClusterNodeTrackerStore();
    private HeartbeatManager heartbeatManager;
    private FailoverManager failoverManager;
    private VertexCacheInternalClient vertexCacheInternalClient = null;
    private String localRoleOverride = null;
    private ClusterNode localNode;


    @Override
    protected void onStart() {
        try {
            this.clusterConfig = Config.getInstance().getClusterConfigLoader();
            this.localNode = clusterConfig.getAllClusterNodes().get(Config.getInstance().getClusterConfigLoader().getLocalNodeId());

            // Register all known cluster nodes into tracker store
            clusterConfig.getAllClusterNodes().values()
                    .forEach(clusterNodeTrackerStore::registerNode);

            // Register a simple logging observer
            clusterNodeTrackerStore.registerListener(new ClusterNodeLoggerObserver());

            // Initialize internal client for intra-cluster messaging
            this.initVertexCacheClient(localNode);

            // Start heartbeat manager
            this.heartbeatManager = new HeartbeatManager(this, getClusterHeartbeatIntervalMs());
            new Thread(heartbeatManager, "ClusterHeartbeatThread").start();

            // Start failover manager
            this.failoverManager = new FailoverManager(this);

            reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Cluster module started successfully");
        } catch (Exception e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, "Exception during cluster initialization: " + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        //if (heartbeatManager != null) {
        //    heartbeatManager.stop();
       // }
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

            reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Cluster nodes validated successfully.");

        } catch (Exception e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, "Cluster validation failed: " + e.getMessage());
            throw e;
        }
    }

    public int getClusterHeartbeatIntervalMs() {
        return Integer.parseInt(clusterConfig.getCoordinationSettings().getOrDefault("cluster_failover_check_interval_ms", "2000"));
    }

    private void initVertexCacheClient(ClusterNode clusterNode) {
        if(this.vertexCacheInternalClient == null) {
            VertexCacheInternalClientOptions options = new VertexCacheInternalClientOptions();
            options.setClientId(clusterNode.getId());
            // Not Required, because secuity can re-use TLS and Public/Private keys if those are enabled
            options.setClientToken("");

            options.setServerHost(clusterNode.getHost());
            options.setServerPort(Integer.parseInt(clusterNode.getPort()));

            if(Config.getInstance().getSecurityConfigLoader().isEncryptTransport()) {
                options.setEnableTlsEncryption(true);
                options.setTlsCertificate(Config.getInstance().getSecurityConfigLoader().getTlsCertificate());

            } else {
                options.setEnableTlsEncryption(false);
            }

            if(Config.getInstance().getSecurityConfigLoader().getEncryptionMode().equals(EncryptionMode.ASYMMETRIC)) {
                options.setEncryptionMode(EncryptionMode.ASYMMETRIC);
                options.setPublicKey(Config.getInstance().getSecurityConfigLoader().getPublicKey().toString());
            } else if(Config.getInstance().getSecurityConfigLoader().getEncryptionMode().equals(EncryptionMode.SYMMETRIC)) {
                options.setEncryptionMode(EncryptionMode.SYMMETRIC);
                options.setSharedEncryptionKey(Config.getInstance().getSecurityConfigLoader().getSharedEncryptionKey());
            } else {
                options.setEncryptionMode(EncryptionMode.NONE);
            }
            options.setEncryptionMode(EncryptionMode.ASYMMETRIC);
        }
    }

    public ClusterConfigLoader getClusterConfig() {
        return clusterConfig;
    }

    public ClusterNode getLocalNode() {
        return localNode;
    }

    public ClusterNodeTrackerStore getClusterNodeTrackerStore() {
        return clusterNodeTrackerStore;
    }

    public List<ClusterNode> getPeers() {
        return clusterConfig.getAllClusterNodes()
                .values()
                .stream()
                .filter(node -> !node.getId().equals(localNode.getId()))
                .toList();
    }

    public ClusterNode getPrimaryNode() {
        return clusterConfig.getAllClusterNodes()
                .values()
                .stream()
                .filter(n -> ClusterNodeRole.PRIMARY.equals(n.getRole()))
                .findFirst()
                .orElse(null);
    }

    public void promoteSelfToPrimary() {
        if (!ClusterNodeRole.SECONDARY.equals(localNode.getRole())) {
            LogHelper.getInstance().logWarn("Local node is not SECONDARY — cannot promote.");
            return;
        }

        LogHelper.getInstance().logInfo("[ClusterModule] Promoting local node '" + localNode.getId() + "' to PRIMARY.");
        this.localRoleOverride = "PRIMARY";

        clusterNodeTrackerStore.notifyRoleChange(localNode.getId(), "PRIMARY");
        reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Local node promoted to PRIMARY.");

        for (ClusterNode peer : getPeers()) {
            sendClusterCommand(peer, "ROLE_CHANGE " + localNode.getId() + " PRIMARY");
        }
    }

    public void pingPeer(ClusterNode peer) {
        try {
          //  sendClusterCommand(peer, "PEER_PING " + localNode.getId() + " " + getCoordinationHash());
        } catch (Exception e) {
            clusterNodeTrackerStore.markNodeDown(peer.getId());
            LogHelper.getInstance().logError("Failed to send heartbeat to peer '" + peer.getId() + "': " + e.getMessage());
        }
    }

    private void sendClusterCommand(ClusterNode peer, String command) {
        try {
          //  VertexCacheInternalClient client = initVertexCacheClient(peer);
          //  client.sendCommand(command);  // assuming method exists in internal client
        } catch (Exception e) {
            LogHelper.getInstance().logError("Failed to send cluster command to peer '" + peer.getId() + "': " + e.getMessage());
        }
    }

    /*
    public ClusterConfigLoader getClusterConfig() {
        return clusterConfig;
    }

    public ClusterNodeTrackerStore getPeerStore() {
        return peerStore;
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
        return "PRIMARY".equalsIgnoreCase(getEffectiveLocalRole());
    }

    public boolean isSecondary() {
        return "SECONDARY".equalsIgnoreCase(getEffectiveLocalRole());
    }

    private String getEffectiveLocalRole() {
        return (localRoleOverride != null) ? localRoleOverride : getLocalNode().role();
    }

    public void pingPeer(ClusterNode peer) {
        try {
            // Send HEARTBEAT command instead of direct peerStore update:
            sendClusterCommand(peer, "HEARTBEAT " + getLocalNode().id());
        } catch (Exception e) {
            peerStore.markPeerDown(peer.id());
            LogHelper.getInstance().logError("Failed to send heartbeat to peer '" + peer.id() + "': " + e.getMessage());
        }
    }

    public void promoteSelfToPrimary() {
        if (!isSecondary()) {
            LogHelper.getInstance().logWarn("Local node is not SECONDARY — cannot promote.");
            return;
        }

        LogHelper.getInstance().logInfo("[ClusterModule] Promoting local node '" + getLocalNode().id() + "' to PRIMARY.");
        this.localRoleOverride = "PRIMARY";

        // Internal notification to listeners
        peerStore.notifyRoleChange(getLocalNode().id(), "PRIMARY");
        reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Local node promoted to PRIMARY.");

        // Send ROLE_CHANGE command to peers
        for (ClusterNode peer : getPeers()) {
            sendClusterCommand(peer, "ROLE_CHANGE " + getLocalNode().id() + " PRIMARY");
        }
    }

    private void sendClusterCommand(ClusterNode peer, String command) {
        try {

            this.initVertexCacheClient(peer);

            // Need to Implement, send command(s)

        } catch (Exception e) {
            LogHelper.getInstance().logError("Failed to send cluster command to peer '" + peer.id() + "': " + e.getMessage());
        }
    }

    private VertexCacheInternalClient initVertexCacheClient(ClusterNode peer) {
        if(this.vertexCacheInternalClient == null) {
            VertexCacheInternalClientOptions options = new VertexCacheInternalClientOptions();
            options.setClientId(peer.id());
            // Not Required, because secuity can re-use TLS and Public/Private keys if those are enabled
            options.setClientToken("");

            options.setServerHost(peer.host());
            options.setServerPort(peer.port());

            if(Config.getInstance().getSecurityConfigLoader().isEncryptTransport()) {
                options.setEnableTlsEncryption(true);
                options.setTlsCertificate(Config.getInstance().getSecurityConfigLoader().getTlsCertificate());

            } else {
                options.setEnableTlsEncryption(false);
            }

            if(Config.getInstance().getSecurityConfigLoader().getEncryptionMode().equals(EncryptionMode.ASYMMETRIC)) {
                options.setEncryptionMode(EncryptionMode.ASYMMETRIC);
                options.setPublicKey(Config.getInstance().getSecurityConfigLoader().getPublicKey().toString());
            } else if(Config.getInstance().getSecurityConfigLoader().getEncryptionMode().equals(EncryptionMode.SYMMETRIC)) {
                options.setEncryptionMode(EncryptionMode.SYMMETRIC);
                options.setSharedEncryptionKey(Config.getInstance().getSecurityConfigLoader().getSharedEncryptionKey());
            } else {
                options.setEncryptionMode(EncryptionMode.NONE);
            }
            options.setEncryptionMode(EncryptionMode.ASYMMETRIC);
        }
        return this.vertexCacheInternalClient;
    }

    public ClusterNodeTrackerStore getClusterNodeTrackerStore() {
        return clusterNodeTrackerStore;
    }

     */


}
