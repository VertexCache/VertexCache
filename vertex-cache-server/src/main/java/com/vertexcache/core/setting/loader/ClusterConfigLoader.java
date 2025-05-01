package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;
import com.vertexcache.module.cluster.meta.ClusterCoordinationKeys;
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

            this.localNodeId = resolveNodeId();

            Map<String, String> all = this.getConfigLoader().getAllProperties();
            Set<String> nodeIds = discoverNodeIds(all);

            for (String id : nodeIds) {
                String prefix = "cluster_node." + id;
                String role = this.getConfigLoader().getProperty(prefix + ".role", null);
                String host = this.getConfigLoader().getProperty(prefix + ".host", null);
                String portStr = this.getConfigLoader().getProperty(prefix + ".port", null);
                String status = this.getConfigLoader().getProperty(prefix + ".status", "active");

                Integer port = null;
                try {
                    if (portStr != null) {
                        port = Integer.parseInt(portStr);
                    }
                } catch (NumberFormatException e) {
                    // Leave port as null if not parseable
                }

                allNodes.put(id, new ClusterNode(id, role, host, port, status));
            }

            loadCoordinationSettings();
        }
    }

    private String resolveNodeId() {
        String fromEnv = System.getenv("CLUSTER_NODE_ID");
        return (fromEnv != null && !fromEnv.isBlank())
                ? fromEnv
                : this.getConfigLoader().getProperty("cluster_node_id", "").trim();
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
            lines.add(String.format("  - %s", node.id()));
            lines.add(String.format("      role:   %s", node.role()));
            lines.add(String.format("      host:   %s", node.host()));
            lines.add(String.format("      port:   %s", (node.port() == 0 ? "not set or invalid" : node.port())));
            lines.add(String.format("      status: %s", node.status()));
        }

        return lines;
    }

    public Map<String, String> getFlatSummary() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("cluster_node_id", localNodeId);
        map.put("cluster_node_count", String.valueOf(allNodes.size()));

        int i = 0;
        for (ClusterNode node : allNodes.values()) {
            map.put(String.format("cluster_peer.%d.id", i), node.id());
            map.put(String.format("cluster_peer.%d.role", i), node.role() != null ? node.role() : "null");
            map.put(String.format("cluster_peer.%d.status", i), node.status() != null ? node.status() : "null");
            map.put(String.format("cluster_peer.%d.host", i), node.host() != null ? node.host() : "null");
            map.put(String.format("cluster_peer.%d.port", i), String.valueOf(node.port()));  // Use 0 if primitive
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
            lines.add(String.format("  - %s", node.id()));
            lines.add(String.format("      role:   %s", node.role() != null ? node.role() : "null"));
            lines.add(String.format("      host:   %s", node.host() != null ? node.host() : "null"));
            lines.add(String.format("      port:   %d", node.port()));  // If int, will print 0 if unset
            lines.add(String.format("      status: %s", node.status() != null ? node.status() : "null"));
        }

        return lines;
    }

    public String getProperty(String key, String fallback) {
        return this.getConfigLoader().getProperty(key, fallback);
    }
}
