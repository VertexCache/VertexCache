package com.vertexcache.module.cluster.observer;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.module.cluster.store.PeerStateListener;

public class PeerStateObserver implements PeerStateListener {

    @Override
    public void onPeerDown(String nodeId) {
        LogHelper.getInstance().logInfo("[PeerObserver] Peer DOWN: " + nodeId);
    }

    @Override
    public void onPeerUp(String nodeId) {
        LogHelper.getInstance().logInfo("[PeerObserver] Peer UP: " + nodeId);
    }

    @Override
    public void onRoleChange(String nodeId, String newRole) {
        LogHelper.getInstance().logInfo("[PeerObserver] Role changed for " + nodeId + " → " + newRole);
    }
}
