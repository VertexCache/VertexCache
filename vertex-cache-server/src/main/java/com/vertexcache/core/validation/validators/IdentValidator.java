package com.vertexcache.core.validation.validators;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.setting.loader.ClusterConfigLoader;
import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.cluster.model.ClusterNode;

import java.util.Map;

public class IdentValidator implements Validator {

    private final String clientId;

    public IdentValidator(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public void validate() {
        new ClientIdValidator(clientId).validate();
        boolean authEnabled = Config.getInstance().getAuthWithTenantConfigLoader().isAuthEnabled();
        boolean clusterEnabled = Config.getInstance().getClusterConfigLoader().isEnableClustering();
        if (clusterEnabled && authEnabled) {
            ClusterConfigLoader configLoader = Config.getInstance().getClusterConfigLoader();
            Map<String, ClusterNode> nodes = configLoader.getAllClusterNodes();

            boolean isInternalClusterClient = nodes.containsKey(clientId)
                    && Config.getInstance().getClusterConfigLoader().getLocalClusterNode().getId().equals(clientId);

            if (nodes.containsKey(clientId) && !isInternalClusterClient) {
                throw new VertexCacheValidationException("Client ID '" + clientId + "' is reserved for internal cluster use.");
            }
        }
    }
}
