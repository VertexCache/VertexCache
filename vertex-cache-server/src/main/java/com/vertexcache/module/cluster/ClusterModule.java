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
import com.vertexcache.module.cluster.heartbeat.HeartbeatManager;
import com.vertexcache.module.cluster.model.ClusterNode;
import com.vertexcache.module.cluster.store.ClusterPeerStore;
import com.vertexcache.module.cluster.observer.PeerStateObserver;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClusterModule extends Module {

    private ClusterConfigLoader clusterConfig;
    private final ClusterPeerStore peerStore = new ClusterPeerStore();
    private HeartbeatManager heartbeatManager;
    private String localRoleOverride = null;
    private VertexCacheInternalClient vertexCacheInternalClient = null;

    @Override
    protected void onStart() {
        try {
            this.clusterConfig = Config.getInstance().getClusterConfigLoader();
            this.peerStore.registerListener(new PeerStateObserver());
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

    public ClusterConfigLoader getClusterConfig() {
        return clusterConfig;
    }

    public ClusterPeerStore getPeerStore() {
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

    public int getHeartbeatIntervalMs() {
        return Integer.parseInt(clusterConfig.getCoordinationSettings().getOrDefault("cluster_failover_check_interval_ms", "2000"));
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
}
