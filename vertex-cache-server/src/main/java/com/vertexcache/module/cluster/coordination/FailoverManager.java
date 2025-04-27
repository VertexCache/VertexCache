package com.vertexcache.module.cluster.coordination;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.module.cluster.model.ClusterNode;
import com.vertexcache.module.cluster.store.PeerState;

import java.util.Comparator;
import java.util.Optional;

public class FailoverManager {

    private final ClusterModule clusterModule;

    public FailoverManager(ClusterModule clusterModule) {
        this.clusterModule = clusterModule;
    }

    public void checkFailover() {
        String failoverEnabled = clusterModule
                .getClusterConfig()
                .getCoordinationSettings()
                .getOrDefault("cluster_failover_enabled", "false");

        if (!failoverEnabled.equalsIgnoreCase("true")) {
            LogHelper.getInstance().logDebug("[FailoverManager] Auto-failover is disabled by configuration.");
            return;
        }

        ClusterNode primaryNode = clusterModule.getPrimaryNode();

        boolean primaryDown = primaryNode == null
                || clusterModule
                .getPeerStore()
                .get(primaryNode.id())
                .map(PeerState::isDown)
                .orElse(true);

        if (!primaryDown) {
            LogHelper.getInstance().logDebug("[FailoverManager] Primary node is healthy, no failover needed.");
            return;
        }

        if (!clusterModule.isSecondary()) {
            LogHelper.getInstance().logDebug("[FailoverManager] Local node is not secondary, not eligible for promotion.");
            return;
        }

        if (!isHighestPriorityCandidate()) {
            LogHelper.getInstance().logDebug("[FailoverManager] Local node is not the highest-priority candidate for failover.");
            return;
        }

        promoteSelfToPrimary();
    }

    private boolean isHighestPriorityCandidate() {
        int localPriority = getLocalFailoverPriority();

        Optional<ClusterNode> competingSecondary = clusterModule.getSecondaryNodes()
                .stream()
                .filter(node -> !node.id().equals(clusterModule.getLocalNode().id()))
                .filter(node -> {
                    // Peer must not be marked down to be a candidate
                    return clusterModule
                            .getPeerStore()
                            .get(node.id())
                            .map(state -> !state.isDown())
                            .orElse(false);
                })
                .min(Comparator.comparingInt(this::getFailoverPriority));

        return competingSecondary
                .map(peer -> localPriority < getFailoverPriority(peer))
                .orElse(true);  // No other eligible secondaries → I am the highest by default
    }

    private int getLocalFailoverPriority() {
        return Integer.parseInt(clusterModule
                .getClusterConfig()
                .getCoordinationSettings()
                .getOrDefault("cluster_failover_priority", "100"));
    }

    private int getFailoverPriority(ClusterNode node) {
        String key = "cluster_node." + node.id() + ".failover_priority";
        return Integer.parseInt(Config.getInstance()
                .getClusterConfigLoader()
                .getConfigLoader()
                .getProperty(key, "100"));
    }

    private void promoteSelfToPrimary() {
        LogHelper.getInstance().logInfo("[FailoverManager] Promoting self to PRIMARY due to primary node failure.");

        // Simplified: Here we just log and change role locally.
        // You would need to update your ClusterNode status or role field here.
        // Could also notify peers in the future.

        // Example:
        // clusterModule.promoteSelfToPrimary();
        // Placeholder:
        LogHelper.getInstance().logInfo("[FailoverManager] Self-promotion logic would be executed here.");
    }
}
