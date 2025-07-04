/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.core.setting.loaders;

import com.vertexcache.core.setting.ConfigKey;
import com.vertexcache.core.setting.model.LoaderBase;
import com.vertexcache.module.cluster.model.ClusterNodeAvailability;
import com.vertexcache.module.cluster.model.ClusterNodeRole;
import com.vertexcache.module.cluster.util.ClusterCoordinationKeys;
import com.vertexcache.module.cluster.model.ClusterNode;

import java.util.*;

/**
 * Configuration loader responsible for parsing and validating clustering-related settings.
 *
 * Handles options such as:
 * - Whether clustering is enabled
 * - Node role (PRIMARY or STANDBY)
 * - Cluster node ID and peer discovery settings
 *
 * Ensures that the cluster subsystem is correctly configured before any inter-node
 * communication or coordination begins.
 *
 * Required for enabling high availability, failover, and peer synchronization in VertexCache.
 */
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

    public int getClusterHeartbeatIntervalMs() {
        return Integer.parseInt(getCoordinationSettings().getOrDefault("cluster_failover_check_interval_ms", "2000"));
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

    public ClusterNode getNodeByNodeId(String nodeId) {
        return allNodes.get(nodeId);
    }

    public ClusterNode getLocalClusterNode() {
        return allNodes.get(localNodeId);
    }

    public ClusterNode getPrimaryEnabledClusterNode() {
        return allNodes.values().stream()
                .filter(n -> n.getRole() == ClusterNodeRole.PRIMARY)
                .filter(n -> n.getAvailability().isEnabled())
                .findFirst()
                .orElse(null);
    }

    public ClusterNode getSecondaryEnabledClusterNode() {
        return getSecondaryNodeByAvailability(true);
    }

    public ClusterNode getSecondaryDisabledClusterNode() {
        return getSecondaryNodeByAvailability(false);
    }

    private ClusterNode getSecondaryNodeByAvailability(boolean enabled) {
        return allNodes.values().stream()
                .filter(n -> n.getRole() == ClusterNodeRole.SECONDARY)
                .filter(n -> n.getAvailability().isEnabled() == enabled)
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns true if the local node is configured as PRIMARY.
     */
    public boolean isPrimaryNode() {
        ClusterNode node = getLocalClusterNode();
        return node != null && node.getRole() == ClusterNodeRole.PRIMARY;
    }

    /**
     * Returns true if the local node is configured as SECONDARY and enabled.
     */
    public boolean isSecondaryNode() {
        ClusterNode node = getLocalClusterNode();
        return node != null &&
                node.getRole() == ClusterNodeRole.SECONDARY &&
                node.getAvailability().isEnabled();
    }

    /**
     * Returns true if the local node is a standby SECONDARY (disabled).
     */
    public boolean isStandbyNode() {
        ClusterNode node = getLocalClusterNode();
        return node != null &&
                node.getRole() == ClusterNodeRole.SECONDARY &&
                !node.getAvailability().isEnabled();
    }
}
