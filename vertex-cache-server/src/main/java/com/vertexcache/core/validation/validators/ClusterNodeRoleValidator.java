package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.ValidatorHandler;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.cluster.ClusterNodeRole;

public class ClusterNodeRoleValidator implements ValidatorHandler<String> {

    @Override
    public void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new VertexCacheValidationException("Cluster node role is required.");
        }

        if (!ClusterNodeRole.isValid(value)) {
            throw new VertexCacheValidationException(
                    "Invalid cluster node role: '" + value + "'. Must be one of: PRIMARY or SECONDARY.");
        }
    }
}
