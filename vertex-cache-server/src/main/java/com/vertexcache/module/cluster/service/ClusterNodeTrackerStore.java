package com.vertexcache.module.cluster.service;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.module.cluster.model.ClusterNode;
import com.vertexcache.module.cluster.model.ClusterNodeHealthStatus;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClusterNodeTrackerStore {

    private final Map<String, ClusterNode> nodesById = new HashMap<>();
    private final List<ClusterNodeEventListener> listeners = new CopyOnWriteArrayList<>();

    public void registerNode(ClusterNode node) {
        nodesById.put(node.getId(), node);
    }

    public Optional<ClusterNode> get(String nodeId) {
        return Optional.ofNullable(nodesById.get(nodeId));
    }

    public void updateHeartbeat(String nodeId) {
        ClusterNode node = nodesById.get(nodeId);
        if (node == null) return;

        node.getHeartbeat().updateHeartbeat();

        LogHelper.getInstance().logInfo("[ClusterNodeTrackerStore] Heartbeat timestamp refreshed for: " + nodeId);

        if (node.getHealthStatus() != ClusterNodeHealthStatus.ACTIVE) {
            node.setHealthStatus(ClusterNodeHealthStatus.ACTIVE);
            notifyNodeUp(nodeId);
        }
    }

    public void markNodeDown(String nodeId) {
        ClusterNode node = nodesById.get(nodeId);
        if (node == null) return;

        if (!node.getHeartbeat().isDown()) {
            node.getHeartbeat().markDown();
            node.setHealthStatus(ClusterNodeHealthStatus.DOWN);
            notifyNodeDown(nodeId);
            LogHelper.getInstance().logWarn("[ClusterNodeTrackerStore] Node marked as DOWN: " + nodeId);
        }
    }

    public void notifyRoleChange(String nodeId, String newRole) {
        for (ClusterNodeEventListener listener : listeners) {
            listener.onRoleChange(nodeId, newRole);
        }
    }

    public void registerListener(ClusterNodeEventListener listener) {
        listeners.add(listener);
    }

    private void notifyNodeDown(String nodeId) {
        for (ClusterNodeEventListener listener : listeners) {
            listener.onNodeDown(nodeId);
        }
    }

    private void notifyNodeUp(String nodeId) {
        for (ClusterNodeEventListener listener : listeners) {
            listener.onNodeUp(nodeId);
        }
    }

    public Collection<ClusterNode> listNodes() {
        return nodesById.values();
    }
}