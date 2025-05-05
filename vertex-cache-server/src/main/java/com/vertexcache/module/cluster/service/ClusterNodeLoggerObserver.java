package com.vertexcache.module.cluster.service;

import com.vertexcache.common.log.LogHelper;

public class ClusterNodeLoggerObserver implements ClusterNodeEventListener {

    @Override
    public void onNodeDown(String nodeId) {
        LogHelper.getInstance().logInfo("[NodeObserver] Node DOWN: " + nodeId);
    }

    @Override
    public void onNodeUp(String nodeId) {
        LogHelper.getInstance().logInfo("[NodeObserver] Node UP: " + nodeId);
    }

    @Override
    public void onRoleChange(String nodeId, String newRole) {
        LogHelper.getInstance().logInfo("[NodeObserver] Role changed for " + nodeId + " → " + newRole);
    }
}
