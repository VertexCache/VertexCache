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
package com.vertexcache.module.restapi.handlers;

import com.vertexcache.core.cache.CacheAccessService;
import com.vertexcache.core.command.impl.GetSecondaryIdxOneCommand;
import com.vertexcache.core.util.message.ResultCode;
import com.vertexcache.core.validation.validators.KeyValidator;
import com.vertexcache.module.restapi.model.ApiParameter;

/**
 * REST handler for retrieving cache values using the secondary index idx1.
 *
 * Validates read access and the idx1 path parameter before querying the cache.
 * Responds with the corresponding value if found, or a not-found error otherwise.
 */
public class GetIdx1Handler extends AbstractRestHandler {

    @Override
    public void _handle() throws Exception {
        logRequest(GetSecondaryIdxOneCommand.COMMAND_KEY);

        if (!isReadOnly()) {
            respondForbiddenAccess(ResultCode.UNAUTHORIZED);
            return;
        }

        String idx1 = this.getPathParam(ApiParameter.IDX1.value());

        try {
            new KeyValidator(ApiParameter.IDX1.value(), idx1).validate();
        } catch (Exception ex) {
            respondBadRequest(ex.getMessage());
            return;
        }

        CacheAccessService cache = new CacheAccessService();
        String value = cache.getBySecondaryIdx1(this.getAuthEntry().getTenantId(), idx1);

        if (value == null) {
            respondNotFound(ResultCode.KEY_NOT_FOUND);
        } else {
            respondOk(ResultCode.CACHE_HIT, value);
        }
    }
}
