package com.vertexcache.module.cluster.service.coordination;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.module.cluster.model.ClusterNode;
import com.vertexcache.module.cluster.model.ClusterNodeAvailability;
import com.vertexcache.module.cluster.model.ClusterNodeRole;

import java.util.Comparator;
import java.util.Optional;

/**
 * Coordinates automatic promotion of a standby node to primary
 * when the primary is considered unreachable. Runs as part of the heartbeat cycle.
 */
public class FailoverManager {

    private final ClusterModule clusterModule;

    public FailoverManager(ClusterModule clusterModule) {
        this.clusterModule = clusterModule;
    }

    /**
     * Invoked by HeartbeatManager to evaluate failover conditions
     * and promote this node if eligible.
     */
    public void checkFailover() {
        if (!isFailoverEnabled()) return;

        ClusterNode local = clusterModule.getLocalNode();
        ClusterNode primary = clusterModule.getPrimaryNode();

        // Already the primary, nothing to do
        if (local.getRole() == ClusterNodeRole.PRIMARY) return;

        // No primary configured
        if (primary == null) {
            LogHelper.getInstance().logWarn("[Failover] No primary node configured.");
            return;
        }

        // If primary is up, no need to failover
        boolean primaryAlive = clusterModule.getClusterNodeTrackerStore()
                .get(primary.getId())
                .map(state -> !state.getHeartbeat().isDown())
                .orElse(false);

        if (primaryAlive) return;

        // Find eligible secondary to promote
        Optional<ClusterNode> candidate = clusterModule.getClusterConfig()
                .getAllClusterNodes()
                .values()
                .stream()
                .filter(n -> n.getRole() == ClusterNodeRole.SECONDARY)
                .filter(n -> n.getAvailability() == ClusterNodeAvailability.ENABLED)
                .filter(n -> clusterModule.getClusterNodeTrackerStore()
                        .get(n.getId())
                        .map(state -> !state.getHeartbeat().isDown())
                        .orElse(false))
                .min(Comparator.comparingInt(this::getFailoverPriority));

        if (candidate.isEmpty()) {
            LogHelper.getInstance().logWarn("[Failover] No eligible secondary found for promotion.");
            return;
        }

        ClusterNode elected = candidate.get();
        if (elected.getId().equals(local.getId())) {
            LogHelper.getInstance().logInfo("[Failover] Promoting self to PRIMARY due to failover.");
            clusterModule.promoteSelfToPrimary();
        }
    }

    private boolean isFailoverEnabled() {
        return Boolean.parseBoolean(clusterModule
                .getClusterConfig()
                .getCoordinationSettings()
                .getOrDefault("cluster_failover_enabled", "false"));
    }

    private int getFailoverPriority(ClusterNode node) {
        String key = "cluster_node." + node.getId() + ".failover_priority";
        return Integer.parseInt(
                clusterModule.getClusterConfig()
                        .getConfigLoader()
                        .getProperty(key, "100")
        );
    }
}
