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

import com.vertexcache.core.validation.model.Validator;
import com.vertexcache.core.validation.exception.VertexCacheValidationException;
import com.vertexcache.module.restapi.model.TokenHeader;

/**
 * Validator that ensures the configured token header for REST API authentication is valid and explicitly allowed.
 *
 * Currently, the only supported header is {@code AUTHORIZATION}.
 * Rejects values like {@code NONE}, {@code UNKNOWN}, or anything not matching known secure headers.
 *
 * This validator enforces that token extraction logic is both intentional and compliant with supported headers.
 */
public class TokenHeaderValidator implements Validator {

    private final TokenHeader header;

    public TokenHeaderValidator(TokenHeader header) {
        this.header = header;
    }

    @Override
    public void validate() {
        if (header == null || header != TokenHeader.AUTHORIZATION) {
            throw new VertexCacheValidationException("REST API requires a valid token header (AUTHORIZATION).");
        }
    }
}
