package com.vertexcache.module.cluster;

import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.setting.loader.ClusterConfigLoader;
import com.vertexcache.core.validation.ValidationBatch;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.core.validation.validators.cluster.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClusterModule extends Module {

    private ClusterConfigLoader clusterConfig;

    @Override
    protected void onStart() {
        try {
            reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Cluster nodes loaded successfully");
        } catch (Exception e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, "Exception during cluster initialization: " + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        this.clusterConfig = null;
        setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

    @Override
    protected void onValidate() {
        try {
            ClusterConfigLoader clusterConfig = Config.getInstance().getClusterConfigLoader();

            if (clusterConfig == null) {
                reportHealth(ModuleStatus.STARTUP_FAILED, "Cluster config loader not initialized.");
                throw new VertexCacheClusterModuleException("Cluster config loader is null.");
            }

            var nodes = clusterConfig.getAllClusterNodes();
            if (nodes.isEmpty()) {
                reportHealth(ModuleStatus.STARTUP_FAILED, "No cluster nodes defined in configuration.");
                throw new VertexCacheClusterModuleException("Cluster node list is empty.");
            }

            ValidationBatch batch = new ValidationBatch();
            int primaryCount = 0;
            int secondaryCount = 0;
            Set<String> endpoints = new HashSet<>();

            for (var node : nodes.values()) {
                String nodeRef = "node[" + node.id() + "]";
                batch.check(nodeRef + ".role", new ClusterNodeRoleValidator(node.role()));
                batch.check(nodeRef + ".status", new ClusterNodeStatusValidator(node.status()));
                batch.check(nodeRef + ".host", new ClusterNodeHostValidator(node.host()));
                batch.check(nodeRef + ".port", new ClusterNodePortValidator(node.port()));

                try {
                    ClusterNodeRole roleEnum = ClusterNodeRole.from(node.role());
                    switch (roleEnum) {
                        case PRIMARY -> primaryCount++;
                        case SECONDARY -> secondaryCount++;
                    }
                } catch (IllegalArgumentException ignored) {
                    // Already handled by role validator.
                }

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
                String summary = batch.getSummary();
                reportHealth(ModuleStatus.STARTUP_FAILED, "Cluster validation failed: " + summary);
                throw new VertexCacheClusterModuleException("Cluster validation failed: " + summary);
            }

            Map<String, String> settings = clusterConfig.getCoordinationSettings();
            try {
                new ClusterCoordinationSettingsValidator(settings).validate();
            } catch (VertexCacheValidationException e) {
                reportHealth(ModuleStatus.STARTUP_FAILED, "Cluster coordination settings validation failed: " + e.getMessage());
                throw new VertexCacheClusterModuleException("Cluster coordination settings validation failed: " + e.getMessage());
            }


            reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Cluster nodes validated successfully.");

        } catch (Exception e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, "Cluster validation failed: " + e.getMessage());
            throw e;
        }
    }

    public ClusterConfigLoader getClusterConfig() {
        return clusterConfig;
    }
}
