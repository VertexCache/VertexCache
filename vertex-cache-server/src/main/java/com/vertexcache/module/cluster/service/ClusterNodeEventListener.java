package com.vertexcache.module.cluster.service;

public interface ClusterNodeEventListener {

    void onNodeDown(String nodeId);

    void onNodeUp(String nodeId);

    void onRoleChange(String nodeId, String newRole);
}
