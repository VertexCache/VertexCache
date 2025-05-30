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
package com.vertexcache.module.cluster.service.coordination;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.module.cluster.model.ClusterNode;

/**
 * FailoverManager handles automatic role transitions and recovery procedures
 * in the VertexCache cluster. It monitors node health and availability,
 * promoting a SECONDARY node to PRIMARY when the current PRIMARY becomes unreachable.
 *
 * This class is central to high availability and resilience, ensuring
 * continuous operation of the cluster by coordinating safe and timely failover events.
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
