package com.vertexcache.module.cluster.store;

public class PeerState {
    private final String nodeId;
    private volatile long lastHeartbeatTime;
    private volatile boolean down;

    public PeerState(String nodeId) {
        this.nodeId = nodeId;
        this.lastHeartbeatTime = System.currentTimeMillis();
        this.down = false;
    }

    public String getNodeId() {
        return nodeId;
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
}