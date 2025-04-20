package com.vertexcache.module.cluster;

import com.vertexcache.common.config.reader.ConfigLoader;
import com.vertexcache.common.log.LogHelper;

import java.util.*;

public class ClusterConfigLoader {

    private final ConfigLoader loader;
    private final String nodeId;
    private ClusterNode localNode;
    private final Map<String, ClusterNode> allNodes = new HashMap<>();
    private final Map<String, String> coordinationSettings = new LinkedHashMap<>();

    public ClusterConfigLoader(ConfigLoader loader) {
        this.loader = loader;
        this.nodeId = resolveNodeId(loader);
        loadAllClusterNodes();
        this.localNode = allNodes.get(nodeId);
        validateLocalNode();
        loadCoordinationSettings();
    }

    private String resolveNodeId(ConfigLoader loader) {
        String fromEnv = System.getenv("CLUSTER_NODE_ID");
        return (fromEnv != null && !fromEnv.isBlank()) ? fromEnv : loader.getProperty("cluster_node_id", "").trim();
    }

    private void loadAllClusterNodes() {
        Map<String, String> all = loader.getAllProperties();
        Set<String> nodeIds = new HashSet<>();

        // Discover all node IDs from keys like cluster_node.node-a.role
        for (String key : all.keySet()) {
            if (key.startsWith("cluster_node.") && key.split("\\.").length == 3) {
                String nodeId = key.split("\\.")[1];
                nodeIds.add(nodeId);
            }
        }

        for (String id : nodeIds) {
            String prefix = "cluster_node." + id;
            String role = loader.getProperty(prefix + ".role");
            String host = loader.getProperty(prefix + ".host");
            String portStr = loader.getProperty(prefix + ".port");
            String status = loader.getProperty(prefix + ".status", "active");

            if (role == null || host == null || portStr == null) {
                LogHelper.getInstance().logFatal("[Cluster] Incomplete definition for node " + id);
                continue;
            }

            int port = Integer.parseInt(portStr);
            allNodes.put(id, new ClusterNode(id, role, host, port, status));
        }
    }

    private void validateLocalNode() {
        if (localNode == null) {
            LogHelper.getInstance().logFatal("[Cluster] Local node ID '" + nodeId + "' not found in cluster_node.* blocks.");
            System.exit(1);
        }
    }

    private void loadCoordinationSettings() {

        Map<String, String> defaults = new LinkedHashMap<>();
        defaults.put("cluster_failover_enabled", "true");
        defaults.put("cluster_failover_check_interval_ms", "2000");
        defaults.put("cluster_failover_backoff_jitter_ms", "500");
        defaults.put("cluster_replication_retry_attempts", "3");
        defaults.put("cluster_replication_retry_interval_ms", "100");
        defaults.put("cluster_replication_queue_ttl_ms", "3000");
        defaults.put("cluster_failover_priority", "100");
        defaults.put("cluster_config_strict", "false");
        defaults.put("cluster_advertise_host", "");
        defaults.put("cluster_advertise_port", "");
        defaults.put("cluster_max_standbys", "2");
        defaults.put("cluster_heartbeat_timeout_ms", "6000");
        defaults.put("cluster_auto_rejoin_role", "none");

        for (Map.Entry<String, String> entry : defaults.entrySet()) {
            String key = entry.getKey();
            String fallback = entry.getValue();
            String value = loader.getProperty(key, fallback);
            coordinationSettings.put(key, value);
        }
    }

    public ClusterNode getLocalNode() {
        return localNode;
    }

    public Map<String, ClusterNode> getAllClusterNodes() {
        return Collections.unmodifiableMap(allNodes);
    }

    public Map<String, String> getCoordinationSettings() {
        return Collections.unmodifiableMap(coordinationSettings);
    }

    public Map<String, String> getFlatSummary() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("cluster_node_id", nodeId);
        map.put("cluster_node_role", localNode.role());
        map.put("cluster_node_status", localNode.status());
        map.put("cluster_node_host", localNode.host());
        map.put("cluster_node_port", String.valueOf(localNode.port()));
        map.put("cluster_node_count", String.valueOf(allNodes.size()));

        int i = 0;
        for (ClusterNode node : allNodes.values()) {
            map.put(String.format("cluster_peer.%d.id", i), node.id());
            map.put(String.format("cluster_peer.%d.role", i), node.role());
            map.put(String.format("cluster_peer.%d.status", i), node.status());
            map.put(String.format("cluster_peer.%d.host", i), node.host());
            map.put(String.format("cluster_peer.%d.port", i), String.valueOf(node.port()));
            i++;
        }

        return map;
    }

    public List<String> getTextSummary() {
        List<String> lines = new ArrayList<>();
        lines.add("Cluster Node ID: " + nodeId);
        lines.add("Role: " + localNode.role().toUpperCase());
        lines.add("Host: " + localNode.host());
        lines.add("Port: " + localNode.port());
        lines.add("Status: " + localNode.status());

        lines.add("Cluster Peers:");
        for (ClusterNode node : allNodes.values()) {
            lines.add(String.format("  - %s (%s, %s) → %s:%d",
                    node.id(), node.role(), node.status(), node.host(), node.port()));
        }
        return lines;
    }
}
