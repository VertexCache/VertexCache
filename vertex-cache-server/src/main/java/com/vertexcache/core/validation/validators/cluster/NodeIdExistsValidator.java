package com.vertexcache.core.validation.validators.cluster;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.core.setting.Config;

public class NodeIdExistsValidator implements Validator {
    private final String nodeId;

    public NodeIdExistsValidator(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public void validate() {
        if (!Config.getInstance().getClusterConfigLoader().getAllClusterNodes().containsKey(nodeId)) {
            throw new VertexCacheValidationException("Unknown node ID: " + nodeId);
        }
    }
}
