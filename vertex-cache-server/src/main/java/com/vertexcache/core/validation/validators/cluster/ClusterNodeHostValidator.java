package com.vertexcache.core.validation.validators.cluster;

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;

import java.util.regex.Pattern;

public class ClusterNodeHostValidator implements Validator {
    private final String host;

    private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9.-]+$");

    public ClusterNodeHostValidator(String host) {
        this.host = host;
    }

    @Override
    public void validate() {
        if (host == null || host.isBlank()) {
            throw new VertexCacheValidationException("Cluster node host is required.");
        }

        String trimmed = host.trim();
        if (!HOSTNAME_PATTERN.matcher(trimmed).matches()) {
            throw new VertexCacheValidationException(
                    "Invalid cluster node host: '" + host + "'. Only alphanumeric, dots, and dashes are allowed."
            );
        }
    }
}
