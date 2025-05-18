package com.vertexcache.module.alert;

import com.vertexcache.module.alert.listeners.ClusterNodeEventListener;

public class AlertModuleNoOp implements ClusterNodeEventListener {
    @Override
    public void onSecondaryNodePromotedToPrimary(String nodeId) {
        // NoOp
    }
}
