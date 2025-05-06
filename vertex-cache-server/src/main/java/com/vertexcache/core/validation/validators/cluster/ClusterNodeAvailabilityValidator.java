package com.vertexcache.core.validation.validators.cluster;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.cluster.model.ClusterNodeAvailability;

public class ClusterNodeAvailabilityValidator implements Validator {
    private final String state;

    public ClusterNodeAvailabilityValidator(String state) {
        this.state = state;
    }

    @Override
    public void validate() {
        try {
            ClusterNodeAvailability.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new VertexCacheValidationException("Invalid cluster state: '" + state + "'. Expected one of: PRIMARY, SECONDARY_ACTIVE, SECONDARY_STANDBY.");
        }
    }
}
