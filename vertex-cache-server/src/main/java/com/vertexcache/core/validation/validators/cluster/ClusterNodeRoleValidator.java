package com.vertexcache.core.validation.validators.cluster;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.cluster.enums.ClusterNodeRole;

public class ClusterNodeRoleValidator implements Validator {
    private final String role;

    public ClusterNodeRoleValidator(String role) {
        this.role = role;
    }

    @Override
    public void validate() {
        try {
            ClusterNodeRole.from(role);
        } catch (IllegalArgumentException e) {
            throw new VertexCacheValidationException(e.getMessage());
        }
    }
}
