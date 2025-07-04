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
import com.vertexcache.core.command.impl.DelCommand;
import com.vertexcache.core.util.message.ResultCode;
import com.vertexcache.core.validation.validators.KeyValidator;
import com.vertexcache.module.restapi.model.ApiParameter;

/**
 * REST handler for processing cache delete (DEL) requests.
 *
 * Validates write access and the presence of a valid key path parameter.
 * If validation passes, deletes the key for the current tenant via CacheAccessService
 * and responds with a success result. Returns appropriate error responses otherwise.
 */
public class DelHandler extends AbstractRestHandler {

    @Override
    public void _handle() throws Exception {
        logRequest(DelCommand.COMMAND_KEY);

        if (!isWritable()) {
            respondForbiddenAccess(ResultCode.UNAUTHORIZED);
            return;
        }

        String key = this.getContext().pathParam(ApiParameter.KEY.value());

        try {
            new KeyValidator(ApiParameter.KEY.value(), key).validate();
        } catch (Exception ex) {
            respondBadRequest(ex.getMessage());
            return;
        }

        CacheAccessService cacheAccessService = new CacheAccessService();
        cacheAccessService.remove(this.getAuthEntry().getTenantId(),key);
        respondOk(ResultCode.VALUE_DELETED);
    }
}
