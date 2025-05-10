package com.vertexcache.module.cluster.service.coordination;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.module.cluster.model.ClusterNode;

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

        if (!isFailoverEnabled())
            return;

        ClusterNode local = clusterModule.getLocalNode(); // Should be Secondary Enabled
        ClusterNode primary = clusterModule.getPrimaryNode();


        if(primary.getHeartbeat().isDown()) {
            LogHelper.getInstance().logInfo("[Failover Manager] Primary DOWN");
            clusterModule.promoteSelfToPrimary();
        } else {
            LogHelper.getInstance().logInfo("[Failover Manager] Primary Node UP");
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
