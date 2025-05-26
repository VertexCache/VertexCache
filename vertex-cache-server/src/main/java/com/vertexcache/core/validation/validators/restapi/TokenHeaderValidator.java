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

import com.vertexcache.core.validation.Validator;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.module.restapi.model.TokenHeader;

public class TokenHeaderValidator implements Validator {

    private final TokenHeader header;

    public TokenHeaderValidator(TokenHeader header) {
        this.header = header;
    }

    @Override
    public void validate() {
        if (header == null || header == TokenHeader.NONE || header == TokenHeader.UNKNOWN) {
            throw new VertexCacheValidationException("Token header must be specified and valid for REST API");
        }
    }
}
