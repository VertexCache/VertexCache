package com.vertexcache.core.validation.validators.cluster;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.cluster.enums.ClusterNodeStatus;

public class ClusterNodeStatusValidator implements Validator {
    private final String status;

    public ClusterNodeStatusValidator(String status) {
        this.status = status;
    }

    @Override
    public void validate() {
        try {
            ClusterNodeStatus.from(status);
        } catch (IllegalArgumentException e) {
            throw new VertexCacheValidationException(e.getMessage());
        }
    }
}
