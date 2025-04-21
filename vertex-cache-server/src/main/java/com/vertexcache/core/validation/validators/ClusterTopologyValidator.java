package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.ValidationBatch;
import com.vertexcache.module.cluster.ClusterNode;
import com.vertexcache.module.cluster.ClusterNodeRole;

import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class ClusterTopologyValidator {

    private final ClusterNodeRoleValidator roleValidator = new ClusterNodeRoleValidator();

    public String validate(Map<String, ClusterNode> nodes) {
        if (nodes == null || nodes.size() < 2) {
            return "Expected at least 2 cluster nodes. Found: " + (nodes == null ? 0 : nodes.size());
        }

        ValidationBatch batch = new ValidationBatch();
        Set<String> endpoints = new HashSet<>();
        int primaryCount = 0;
        int secondaryCount = 0;

        for (ClusterNode node : nodes.values()) {
            if (node.host() == null || node.host().isBlank()) {
                batch.getErrors().add("Node '" + node.id() + "' has missing host.");
            }

            if (node.port() <= 0 || node.port() > 65535) {
                batch.getErrors().add("Node '" + node.id() + "' has invalid port: " + node.port());
            }

            batch.check("node[" + node.id() + "].role", roleValidator, node.role());

            try {
                ClusterNodeRole role = ClusterNodeRole.from(node.role());
                switch (role) {
                    case PRIMARY -> primaryCount++;
                    case SECONDARY -> secondaryCount++;
                }
            } catch (IllegalArgumentException ex) {
                batch.getErrors().add("Node '" + node.id() + "' has invalid role: " + node.role());
            }

            String endpoint = node.host() + ":" + node.port();
            if (!endpoints.add(endpoint)) {
                batch.getErrors().add("Duplicate host:port detected: " + endpoint);
            }
        }

        if (primaryCount != 1) {
            batch.getErrors().add("Expected exactly 1 primary node. Found: " + primaryCount);
        }

        if (secondaryCount < 1) {
            batch.getErrors().add("Expected at least 1 secondary node. Found: " + secondaryCount);
        }

        return batch.hasErrors() ? batch.getSummary() : null;
    }
}
