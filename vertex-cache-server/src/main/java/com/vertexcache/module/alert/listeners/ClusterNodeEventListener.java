package com.vertexcache.module.alert.listeners;

public interface ClusterNodeEventListener {

    void onSecondaryNodePromotedToPrimary(String nodeId);

}
