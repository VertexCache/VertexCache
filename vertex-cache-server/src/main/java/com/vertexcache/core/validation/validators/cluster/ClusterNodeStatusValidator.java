package com.vertexcache.core.validation.validators.cluster;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.cluster.model.ClusterNodeHealthStatus;

public class ClusterNodeStatusValidator implements Validator {
    private final String status;

    public ClusterNodeStatusValidator(String status) {
        this.status = status;
    }

    @Override
    public void validate() {
        try {
            ClusterNodeHealthStatus.from(status);
        } catch (IllegalArgumentException e) {
            throw new VertexCacheValidationException(e.getMessage());
        }
    }
}
