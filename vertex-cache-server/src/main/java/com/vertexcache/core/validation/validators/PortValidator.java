package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;

public class PortValidator implements Validator {

    private final int port;
    private final String label;

    public PortValidator(int port, String label) {
        this.port = port;
        this.label = label != null ? label : "Port";
    }

    @Override
    public void validate() {
        if (port <= 1024 || port > 65535) {
            throw new VertexCacheValidationException(
                    String.format("%s must be between 1025 and 65535 (was %d)", label, port)
            );
        }
    }
}
