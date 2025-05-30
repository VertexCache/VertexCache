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
import com.vertexcache.core.command.impl.SetCommand;
import com.vertexcache.core.util.message.ResultCode;
import com.vertexcache.core.validation.exception.VertexCacheValidationException;
import com.vertexcache.core.validation.validators.KeyValidator;
import com.vertexcache.core.validation.validators.ValueValidator;
import com.vertexcache.module.restapi.model.ApiParameter;

/**
 * REST handler for processing cache insertion or update (SET) requests.
 *
 * Validates write access, required fields (key and value), and optional index fields.
 * Ensures that idx2 is not provided without idx1. Performs format validation if specified.
 *
 * Stores the value in the cache under the specified key and optional indexes,
 * and responds with a success status and echo of the value.
 */
public class SetHandler extends AbstractRestHandler {

    @Override
    public void _handle() throws Exception {
        logRequest(SetCommand.COMMAND_KEY);

        if (!isWritable()) {
            respondForbiddenAccess(ResultCode.UNAUTHORIZED);
            return;
        }

        String key = getStringField(this.getBody(), ApiParameter.KEY.value());
        String value = getStringField(this.getBody(), ApiParameter.VALUE.value());
        String idx1 = getStringField(this.getBody(), ApiParameter.IDX1.value());
        String idx2 = getStringField(this.getBody(), ApiParameter.IDX2.value());
        String formatStr = getStringField(this.getBody(), ApiParameter.FORMAT.value());

        if (key == null) {
            respondBadRequest(ResultCode.KEY_REQUIRED);
            return;
        }

        if(value == null) {
            respondBadRequest(ResultCode.VALUE_REQUIRED);
            return;
        }

        if (idx2 != null && idx1 == null) {
            respondBadRequest(ResultCode.IDX2_REQUIRES_IDX1);
            return;
        }

        try {
            new KeyValidator(ApiParameter.KEY.value(), key).validate();
            new ValueValidator(ApiParameter.VALUE.value(), value, parseDataType(formatStr)).validate();
            if (idx1 != null) new KeyValidator(ApiParameter.IDX1.value(), idx1).validate();
            if (idx2 != null) new KeyValidator(ApiParameter.IDX2.value(), idx2).validate();
        } catch (VertexCacheValidationException ex) {
            respondBadRequest(ex.getMessage());
            return;
        }

        CacheAccessService cache = new CacheAccessService();

        if (idx1 != null && idx2 != null) {
            cache.put(this.getAuthEntry().getTenantId(), key, value, idx1, idx2);
        } else if (idx1 != null) {
            cache.put(this.getAuthEntry().getTenantId(), key, value, idx1);
        } else {
            cache.put(this.getAuthEntry().getTenantId(), key, value);
        }
        respondOk(ResultCode.VALUE_SET, value);
    }
}
