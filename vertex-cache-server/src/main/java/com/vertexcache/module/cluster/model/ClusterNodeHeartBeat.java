package com.vertexcache.module.cluster.model;

public class ClusterNodeHeartBeat {

    private volatile long lastHeartbeatTime;
    private volatile boolean down;

    public ClusterNodeHeartBeat() {
        this.lastHeartbeatTime = System.currentTimeMillis();
        this.down = false;
    }

    public long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public boolean isDown() {
        return down;
    }

    public void updateHeartbeat() {
        this.lastHeartbeatTime = System.currentTimeMillis();
        this.down = false;
    }

    public void markDown() {
        this.down = true;
    }

    public boolean isAlive(long timeoutMs) {
        return !down && (System.currentTimeMillis() - lastHeartbeatTime) <= timeoutMs;
    }
}
