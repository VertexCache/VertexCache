package com.vertexcache.module.cluster.service.heartbeat;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.module.cluster.model.ClusterNode;
import com.vertexcache.module.cluster.service.coordination.FailoverManager;

import java.util.List;
import java.util.concurrent.*;

/**
 * Responsible for periodically sending heartbeat (PEER_PING) messages to all peer nodes
 * and checking cluster failover conditions. Runs on a dedicated daemon thread to avoid
 * blocking socket or application-level operations.
 */
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
            t.setDaemon(true); // Ensures this thread won't block JVM shutdown
            return t;
        });
    }

    /**
     * Starts the periodic heartbeat loop if not already running.
     */
    public void start() {

        System.out.print("=========> HeartbeatManager::start()");

        if (scheduledTask != null && !scheduledTask.isCancelled())
            return;

        scheduledTask = scheduler.scheduleAtFixedRate(
                this::heartbeatLoop,
                0,
                heartbeatIntervalMs,
                TimeUnit.MILLISECONDS
        );

        LogHelper.getInstance().logInfo("[HeartbeatManager] Heartbeat loop started.");
    }

    /**
     * Gracefully shuts down the heartbeat loop and thread.
     */
    public void shutdown() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(true);
        }
        scheduler.shutdownNow();
        LogHelper.getInstance().logInfo("[HeartbeatManager] Heartbeat loop stopped.");
    }

    /**
     * Called on each interval to send PEER_PINGs to all known peers and evaluate failover conditions.
     */
    private void heartbeatLoop() {
        try {
            List<ClusterNode> clusterNodes = clusterModule.getPeers();
            for (ClusterNode clusterNode : clusterNodes) {
                clusterModule.clusterPing(clusterNode); // Send heartbeat to peer
            }

            failoverManager.checkFailover(); // Detect and promote standby if needed

        } catch (Exception e) {
            LogHelper.getInstance().logError("[HeartbeatManager] Heartbeat error: " + e.getMessage());
        }
    }
}
