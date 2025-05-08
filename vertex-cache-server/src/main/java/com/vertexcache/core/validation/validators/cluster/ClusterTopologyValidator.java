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
        int enabledSecondaries = 0;
        Set<String> endpoints = new HashSet<>();

        for (ClusterNode clusterNode : nodes.values()) {
            String nodeRef = "clusterNode[" + clusterNode.getId() + "]";
            batch.check(nodeRef + ".role", new ClusterNodeRoleValidator(clusterNode.getRole().toString()));
            batch.check(nodeRef + ".enabled", new ClusterNodeAvailabilityValidator(clusterNode.getAvailability().toString()));
            batch.check(nodeRef + ".host", new ClusterNodeHostValidator(clusterNode.getHost()));
            batch.check(nodeRef + ".port", new ClusterNodePortValidator(Integer.parseInt(clusterNode.getPort())));

            try {
                switch (clusterNode.getRole().toString().toUpperCase()) {
                    case "PRIMARY" -> primaryCount++;
                    case "SECONDARY" -> {
                        secondaryCount++;
                        if (clusterNode.getAvailability().isEnabled()) {
                            enabledSecondaries++;
                        }
                    }
                }
            } catch (Exception ignored) {
                // Role validator already handles invalid role errors.
            }

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

        if (enabledSecondaries > 1) {
            batch.getErrors().add("Topology: Only 1 SECONDARY node may be enabled for failover; found: " + enabledSecondaries);
        }

        if (batch.hasErrors()) {
            throw new VertexCacheValidationException("Cluster topology validation failed: " + batch.getSummary());
        }
    }

}
