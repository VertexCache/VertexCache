package com.vertexcache.module.cluster.service.coordination;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.module.cluster.model.ClusterNode;
import com.vertexcache.module.cluster.model.ClusterNodeAvailability;
import com.vertexcache.module.cluster.model.ClusterNodeRole;

import java.util.Comparator;
import java.util.Optional;

public class FailoverManager {

    private final ClusterModule clusterModule;

    public FailoverManager(ClusterModule clusterModule) {
        this.clusterModule = clusterModule;
    }

    /*
    public void checkAndPromoteIfNecessary() {
        boolean failoverEnabled = Boolean.parseBoolean(clusterModule.getClusterConfig()
                .getCoordinationSettings()
                .getOrDefault("cluster_failover_enabled", "false"));

        if (!failoverEnabled) return;

        ClusterNode local = clusterModule.getLocalNode();
        ClusterNode primaryNode = clusterModule.getPrimaryNode();

        if (primaryNode == null) {
            LogHelper.getInstance().logWarn("[Failover] No primary node configured.");
            return;
        }

        // Check if primary is down using tracker store
        boolean primaryDown = clusterModule.getClusterNodeTrackerStore()
                .get(primaryNode.getId())
                .map(n -> n.getHeartbeat().isDown())
                .orElse(true);

        if (!primaryDown) return;

        // Find eligible secondary
        Optional<ClusterNode> eligibleSecondary = clusterModule.getClusterConfig()
                .getAllClusterNodes()
                .values()
                .stream()
                .filter(n -> n.role() == ClusterNodeRole.SECONDARY)
                .filter(n -> n.availability() == ClusterNodeAvailability.ENABLED)
                .filter(n -> clusterModule.getClusterNodeTrackerStore()
                        .get(n.id())
                        .map(node -> !node.getHeartbeat().isDown())
                        .orElse(false))
                .min(Comparator.comparingInt(this::getFailoverPriority));

        if (eligibleSecondary.isEmpty()) {
            LogHelper.getInstance().logWarn("[Failover] No eligible secondary node found for promotion.");
            return;
        }

        if (eligibleSecondary.get().getId().equals(local.getId())) {
            LogHelper.getInstance().logInfo("[Failover] Promoting self to PRIMARY due to primary node failure.");
            clusterModule.promoteSelfToPrimary();
        }
    }

    private int getFailoverPriority(ClusterNode node) {
        String key = "cluster_node." + node.getId() + ".failover_priority";
        return Integer.parseInt(clusterModule
                .getClusterConfig()
                .getConfigLoader()
                .getProperty(key, "100"));
    }
    */
}