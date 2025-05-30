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
package com.vertexcache.core.validation.validators;

import com.vertexcache.core.validation.model.Validator;
import com.vertexcache.core.validation.exception.VertexCacheValidationException;

/**
 * PortValidator is used to validate that a given input string represents a valid TCP/UDP port number.
 * It ensures the value is a numeric string within the valid port range of 1 to 65535.
 *
 * This validator is commonly used in configuration and cluster settings to confirm that
 * network ports are correctly specified before the system attempts to bind or connect.
 */
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
