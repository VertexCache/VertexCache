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
import com.vertexcache.module.cluster.model.ClusterNodeHealthStatus;

/**
 * Validator that ensures a given cluster node health status string is valid.
 *
 * Validates that the input corresponds to a supported enum value from {@code ClusterNodeHealthStatus},
 * such as UP, DOWN, or UNKNOWN.
 *
 * Throws a VertexCacheValidationException if the status is invalid or unrecognized.
 * Typically used to validate health state updates or configuration overrides in clustered environments.
 */
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
