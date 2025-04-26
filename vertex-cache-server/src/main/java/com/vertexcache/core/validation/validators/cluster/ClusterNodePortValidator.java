package com.vertexcache.core.validation.validators.cluster;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;

public class ClusterNodePortValidator implements Validator {
    private final int port;

    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;

    public ClusterNodePortValidator(int port) {
        this.port = port;
    }

    @Override
    public void validate() {
        if (port < MIN_PORT || port > MAX_PORT) {
            throw new VertexCacheValidationException(
                    "Cluster node port '" + port + "' is out of valid range (" + MIN_PORT + "-" + MAX_PORT + ")."
            );
        }
    }
}
