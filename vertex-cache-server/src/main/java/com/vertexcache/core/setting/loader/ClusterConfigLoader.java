package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.setting.ConfigKey;
import com.vertexcache.module.cluster.model.ClusterNodeAvailability;
import com.vertexcache.module.cluster.model.ClusterNodeHealthStatus;
import com.vertexcache.module.cluster.model.ClusterNodeRole;
import com.vertexcache.module.cluster.util.ClusterCoordinationKeys;
import com.vertexcache.module.cluster.model.ClusterNode;

import java.util.*;

public class ClusterConfigLoader extends LoaderBase {

    private boolean enableClustering;
    private String localNodeId;
    private final Map<String, ClusterNode> allNodes = new HashMap<>();
    private final Map<String, String> coordinationSettings = new LinkedHashMap<>();

    @Override
    public void load() {
        this.enableClustering = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_CLUSTERING,ConfigKey.ENABLE_CLUSTERING_DEFAULT);

        if(this.enableClustering) {

            this.localNodeId = this.getConfigLoader().getProperty(ConfigKey.CLUSTER_NODE_ID);

            Map<String, String> all = this.getConfigLoader().getAllProperties();
            Set<String> nodeIds = discoverNodeIds(all);

            for (String id : nodeIds) {
                String prefix = "cluster_node." + id;
                String role = this.getConfigLoader().getProperty(prefix + ".role", null);
                String host = this.getConfigLoader().getProperty(prefix + ".host", null);
                String port = this.getConfigLoader().getProperty(prefix + ".port", null);
                String enabled = this.getConfigLoader().getProperty(prefix + ".enabled", null);
               // String status = this.getConfigLoader().getProperty(prefix + ".status", null);

                allNodes.put(id, new ClusterNode(
                        id,
                        host,
                        port,
                        ClusterNodeRole.from(role),
                        ClusterNodeAvailability.from(enabled)
                ));
            }

            loadCoordinationSettings();
        }
    }

    private Set<String> discoverNodeIds(Map<String, String> properties) {
        Set<String> nodeIds = new HashSet<>();
        for (String key : properties.keySet()) {
            if (key.startsWith("cluster_node.") && key.split("\\.").length == 3) {
                String nodeId = key.split("\\.")[1];
                nodeIds.add(nodeId);
            }
        }
        return nodeIds;
    }

    private void loadCoordinationSettings() {
        for (String key : ClusterCoordinationKeys.ACTIVE_KEYS) {
            String value = this.getConfigLoader().getProperty(key, null);
            if (value != null) {
                coordinationSettings.put(key, value);
            }
        }
    }

    public boolean isEnableClustering() {
        return enableClustering;
    }

    public void setEnableClustering(boolean enableClustering) {
        this.enableClustering = enableClustering;
    }

    public String getLocalNodeId() {
        return localNodeId;
    }

    public Map<String, ClusterNode> getAllClusterNodes() {
        return Collections.unmodifiableMap(allNodes);
    }

    public Map<String, String> getCoordinationSettings() {
        return Collections.unmodifiableMap(coordinationSettings);
    }

    public List<String> getAttributeSummary() {
        List<String> lines = new ArrayList<>();
        lines.add("Cluster Node Attributes:");

        for (ClusterNode node : allNodes.values()) {
            lines.add(String.format("  - %s", node.getId()));
            lines.add(String.format("      role:         %s", node.getRole()));
            lines.add(String.format("      host:         %s", node.getHost()));
            lines.add(String.format("      port:         %s", (node.getPort() == null || node.getPort().isBlank() ? "not set or invalid" : node.getPort())));
            lines.add(String.format("      enabled: %s", node.getAvailability()));
        }

        return lines;
    }

    public Map<String, String> getFlatSummary() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("cluster_node_id", localNodeId);
        map.put("cluster_node_count", String.valueOf(allNodes.size()));

        int i = 0;
        for (ClusterNode node : allNodes.values()) {
            map.put(String.format("cluster_peer.%d.id", i), node.getId());
            map.put(String.format("cluster_peer.%d.role", i), node.getRole() != null ? String.valueOf(node.getRole()) : "null");
            map.put(String.format("cluster_peer.%d.enabled", i), node.getAvailability() != null ? String.valueOf(node.getAvailability()) : "null");
            map.put(String.format("cluster_peer.%d.host", i), node.getHost() != null ? node.getHost() : "null");
            map.put(String.format("cluster_peer.%d.port", i), String.valueOf(node.getPort()));  // Use 0 if primitive
            i++;
        }

        return map;
    }

    public List<String> getTextSummary() {
        List<String> lines = new ArrayList<>();
        lines.add("Cluster Node ID: " + localNodeId);
        lines.add("Node Count: " + allNodes.size());
        lines.add("Cluster Peers:");



        for (ClusterNode node : allNodes.values()) {
            String port = node.getPort();
            lines.add(String.format("  - %s", node.getId()));
            lines.add(String.format("      role:   %s", node.getRole() != null ? node.getRole() : "null"));
            lines.add(String.format("      host:   %s", node.getHost() != null ? node.getHost() : "null"));
            lines.add(String.format("      port:   %s", (port == null || port.isBlank()) ? "not set or invalid" : port));
            lines.add(String.format("      enabled: %s", node.getAvailability() != null ? node.getAvailability() : "null"));
        }

        return lines;
    }

    public String getProperty(String key, String fallback) {
        return this.getConfigLoader().getProperty(key, fallback);
    }

    /**
     * Returns the ClusterNode with the given ID, or null if not found.
     */
    public ClusterNode getNodeByNodeId(String nodeId) {
        return allNodes.get(nodeId);
    }

    /**
     * Returns the local ClusterNode based on cluster_node_id.
     */
    public ClusterNode getLocalClusterNode() {
        return allNodes.get(localNodeId);
    }
}
