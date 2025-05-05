package com.vertexcache.core.validation.validators.cluster;

import com.vertexcache.core.validation.ValidationBatch;
import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.cluster.model.ClusterNode;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClusterTopologyValidator implements Validator {
    private final Map<String, ClusterNode> nodes;

    public ClusterTopologyValidator(Map<String, ClusterNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public void validate() {
        ValidationBatch batch = new ValidationBatch();
        int primaryCount = 0;
        int secondaryCount = 0;
        Set<String> endpoints = new HashSet<>();

        for (ClusterNode clusterNode : nodes.values()) {
            String nodeRef = "clusterNode[" + clusterNode.getId() + "]";
            batch.check(nodeRef + ".role", new ClusterNodeRoleValidator(clusterNode.getRole().toString()));
            batch.check(nodeRef + ".status", new ClusterNodeStatusValidator(clusterNode.getHealthStatus().toString()));
            batch.check(nodeRef + ".host", new ClusterNodeHostValidator(clusterNode.getHost()));
            batch.check(nodeRef + ".port", new ClusterNodePortValidator(Integer.parseInt(clusterNode.getPort())));

            try {
                String role = String.valueOf(clusterNode.getRole());
                if (role != null && !role.isBlank()) {
                    switch (role.trim().toUpperCase()) {
                        case "PRIMARY" -> primaryCount++;
                        case "SECONDARY" -> secondaryCount++;
                    }
                }
            } catch (Exception ignored) {
                // Role validator already handles invalid role errors.
            }

            // Duplicate endpoint detection
            String endpoint = clusterNode.getHost() + ":" + clusterNode.getPort();
            if (!endpoints.add(endpoint)) {
                batch.getErrors().add("Topology: Duplicate host:port detected for " + endpoint);
            }
        }

        if (primaryCount != 1) {
            batch.getErrors().add("Topology: Exactly 1 PRIMARY node required, found: " + primaryCount);
        }

        if (secondaryCount < 1) {
            batch.getErrors().add("Topology: At least 1 SECONDARY node required, found: " + secondaryCount);
        }

        if (batch.hasErrors()) {
            throw new VertexCacheValidationException("Cluster topology validation failed: " + batch.getSummary());
        }
    }
}
