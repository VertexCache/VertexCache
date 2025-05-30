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

/**
 * Validator that ensures a cluster node's port number falls within a valid range.
 *
 * Accepts only ports in the range 1024 to 65535, rejecting reserved or invalid values.
 * Intended to enforce safe and non-conflicting port configurations for peer communication.
 *
 * Throws a VertexCacheValidationException if the port is out of range.
 */
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
