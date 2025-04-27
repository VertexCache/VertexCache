package com.vertexcache.module.cluster.heartbeat;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.module.cluster.model.ClusterNode;

import java.util.List;

public class HeartbeatManager implements Runnable {

    private final ClusterModule clusterModule;
    private final int heartbeatIntervalMs;
    private volatile boolean running = true;

    public HeartbeatManager(ClusterModule clusterModule, int heartbeatIntervalMs) {
        this.clusterModule = clusterModule;
        this.heartbeatIntervalMs = heartbeatIntervalMs;
    }

    @Override
    public void run() {
        while (running) {
            try {
                List<ClusterNode> peers = clusterModule.getPeers();
                for (ClusterNode peer : peers) {
                    clusterModule.pingPeer(peer);
                }
                Thread.sleep(heartbeatIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                LogHelper.getInstance().logError("Heartbeat error: " + e.getMessage());
            }
        }
    }

    public void stop() {
        this.running = false;
    }
}