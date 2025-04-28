package com.vertexcache.module.cluster.store;

public interface PeerStateListener {
    void onPeerDown(String nodeId);
    void onPeerUp(String nodeId);
    void onRoleChange(String nodeId, String newRole);
}