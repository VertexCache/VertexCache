package com.vertexcache.core.validation.validators;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.cluster.model.ClusterNode;

import java.util.Map;

public class IdentValidator implements Validator {

    private final String clientId;
    private final boolean isClusterEnabled;

    public IdentValidator(String clientId, boolean isClusterEnabled) {
        this.clientId = clientId;
        this.isClusterEnabled = isClusterEnabled;
    }

    @Override
    public void validate() {
        new ClientIdValidator(clientId).validate();
        boolean authEnabled = Config.getInstance().getAuthWithTenantConfigLoader().isAuthEnabled();
        if (isClusterEnabled && authEnabled) {
            Map<String, ClusterNode> nodes = Config.getInstance().getClusterConfigLoader().getAllClusterNodes();
            if (nodes.containsKey(clientId)) {
                throw new VertexCacheValidationException("Client ID '" + clientId + "' is reserved for internal cluster use.");
            }
        }
    }
}
