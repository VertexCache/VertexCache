package com.vertexcache.core.validation.validators.cluster;

import com.vertexcache.core.validation.ValidationBatch;
import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.cluster.ClusterNode;

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

        for (ClusterNode node : nodes.values()) {
            String nodeRef = "node[" + node.id() + "]";
            batch.check(nodeRef + ".role", new ClusterNodeRoleValidator(node.role()));
            batch.check(nodeRef + ".status", new ClusterNodeStatusValidator(node.status()));
            batch.check(nodeRef + ".host", new ClusterNodeHostValidator(node.host()));
            batch.check(nodeRef + ".port", new ClusterNodePortValidator(node.port()));

            try {
                String role = node.role();
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
            String endpoint = node.host() + ":" + node.port();
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
