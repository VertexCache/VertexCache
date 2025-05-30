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
package com.vertexcache.core.validation.validators.restapi;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.model.Validator;
import com.vertexcache.core.validation.exception.VertexCacheValidationException;

/**
 * Validator that ensures required TLS settings are present when the REST API mandates TLS.
 *
 * If REST API configuration specifies that TLS is required, this validator checks:
 * - A non-blank TLS certificate is configured
 * - A non-blank TLS private key is configured
 *
 * Throws a VertexCacheValidationException if any required TLS file is missing.
 * Ensures that secure REST API access is correctly enforced at startup.
 */
public class RestApiTlsValidator implements Validator {

    @Override
    public void validate() {
        var restConfig = Config.getInstance().getRestApiConfigLoader();
        var tlsConfig = Config.getInstance().getSecurityConfigLoader();

        if (restConfig.isRequireTls()) {
            String cert = tlsConfig.getTlsCertificate();
            String key = tlsConfig.getTlsPrivateKey();

            if (cert == null || cert.isBlank()) {
                throw new VertexCacheValidationException("TLS is required for REST API, but no certificate is set.");
            }

            if (key == null || key.isBlank()) {
                throw new VertexCacheValidationException("TLS is required for REST API, but no private key is set.");
            }
        }
    }
}

