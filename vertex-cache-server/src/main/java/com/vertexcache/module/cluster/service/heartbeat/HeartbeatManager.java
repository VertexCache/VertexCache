package com.vertexcache.module.cluster.service.heartbeat;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.module.cluster.model.ClusterNode;
import com.vertexcache.module.cluster.service.coordination.FailoverManager;

import java.util.concurrent.*;

public class HeartbeatManager {

    private final ClusterModule clusterModule;
    private final int heartbeatIntervalMs;
    private final FailoverManager failoverManager;
    private final ScheduledExecutorService scheduler;

    private ScheduledFuture<?> scheduledTask;

    public HeartbeatManager(ClusterModule clusterModule, int heartbeatIntervalMs) {
        this.clusterModule = clusterModule;
        this.heartbeatIntervalMs = heartbeatIntervalMs;
        this.failoverManager = new FailoverManager(clusterModule);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ClusterHeartbeatThread");
            t.setDaemon(true);
            return t;
        });
    }

    public void start() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) return;

        scheduledTask = scheduler.scheduleAtFixedRate(
                this::heartbeatLoop,
                0,
                heartbeatIntervalMs,
                TimeUnit.MILLISECONDS
        );

        LogHelper.getInstance().logInfo("[HeartbeatManager] Heartbeat loop scheduled at interval " + heartbeatIntervalMs + "ms.");
    }

    public void shutdown() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(true);
        }
        scheduler.shutdownNow();
        LogHelper.getInstance().logInfo("[HeartbeatManager] Heartbeat loop stopped.");
    }

    private void heartbeatLoop() {
        LogHelper.getInstance().logInfo("[HeartbeatManager] Running heartbeat loop");

        try {
            ClusterNode target = null;

            if (clusterModule.getClusterConfig().isPrimaryNode()) {
                target = clusterModule.getClusterConfig().getSecondaryEnabledClusterNode();
            } else if (clusterModule.getClusterConfig().isSecondaryNode()) {
                target = clusterModule.getClusterConfig().getPrimaryEnabledClusterNode();
            }

            if (target != null) {
                LogHelper.getInstance().logInfo("[HeartbeatManager] Sending PEER_PING to node: " + target.getId());
                clusterModule.clusterPing(target);
            } else {
                LogHelper.getInstance().logInfo("[HeartbeatManager] No heartbeat target (likely standby/disabled).");
            }

            // Passive timeout check for all other nodes
            for (ClusterNode node : clusterModule.getClusterConfig().getAllClusterNodes().values()) {
                if (!node.getId().equals(clusterModule.getLocalNode().getId())) {
                    clusterModule.getClusterNodeTrackerStore()
                            .get(node.getId())
                            .ifPresent(tracked -> {
                                if (tracked.getHeartbeat().isDown()) {
                                    clusterModule.getClusterNodeTrackerStore().markNodeDown(node.getId());
                                }
                            });
                }
            }

            failoverManager.checkFailover();

        } catch (Exception e) {
            LogHelper.getInstance().logError("[HeartbeatManager] Heartbeat loop failed: " + e.getMessage());
        }
    }
}