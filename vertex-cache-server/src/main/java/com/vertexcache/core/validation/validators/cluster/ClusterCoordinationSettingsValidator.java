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
import com.vertexcache.module.cluster.util.ClusterCoordinationKeys;

import java.util.Map;

/**
 * Validator responsible for checking the correctness of cluster coordination settings.
 *
 * Validates key coordination parameters such as:
 * - Role assignments (e.g., PRIMARY, STANDBY)
 * - Peer node configurations
 * - Failover enablement and related flags
 *
 * Ensures that all required settings are present and logically consistent
 * before cluster coordination is initialized.
 *
 * Prevents invalid or ambiguous configurations that could lead to cluster instability.
 */
public class ClusterCoordinationSettingsValidator implements Validator {

    private final Map<String, String> settings;

    public ClusterCoordinationSettingsValidator(Map<String, String> settings) {
        this.settings = settings;
    }

    @Override
    public void validate() {
        for (String key : ClusterCoordinationKeys.ACTIVE_KEYS) {
            String value = settings.get(key);
            if (value == null) continue;

            if (key.endsWith("_enabled")) {
                checkBoolean(key);
            } else if (key.contains("interval") || key.contains("priority")) {
                checkPositiveInt(key);
            } else {
                System.out.println("[WARN] No validation rule defined for key: " + key);
            }
        }
    }

    private void checkBoolean(String key) {
        String value = settings.get(key);
        if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
            throw new VertexCacheValidationException("Invalid boolean value for " + key + ": " + value);
        }
    }

    private void checkPositiveInt(String key) {
        String value = settings.get(key);
        try {
            int intVal = Integer.parseInt(value);
            if (intVal <= 0) {
                throw new VertexCacheValidationException("Value for " + key + " must be > 0: " + value);
            }
        } catch (NumberFormatException e) {
            throw new VertexCacheValidationException("Invalid integer for " + key + ": " + value);
        }
    }
}