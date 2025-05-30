/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.core.validation.validators.cluster;

import com.vertexcache.core.validation.model.Validator;
import com.vertexcache.core.validation.exception.VertexCacheValidationException;

import java.util.regex.Pattern;

/**
 * Validator that checks whether a given cluster node host string is valid.
 *
 * Ensures that the host is:
 * - Non-null and non-blank
 * - Contains only alphanumeric characters, dots, and dashes
 *
 * This validator is typically used during configuration loading to verify
 * that a cluster node's hostname or IP address meets basic format expectations.
 *
 * Throws a VertexCacheValidationException if the input is invalid.
 */
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
