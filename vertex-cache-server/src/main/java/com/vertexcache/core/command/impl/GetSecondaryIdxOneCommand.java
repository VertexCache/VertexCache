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
package com.vertexcache.core.command.impl;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.util.StringUtil;
import com.vertexcache.core.cache.CacheAccessService;
import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.validation.validators.KeyValidator;
import com.vertexcache.module.restapi.model.ApiParameter;
import com.vertexcache.server.session.ClientSessionContext;

/**
 * Command used to retrieve all cache entries that match a given secondary index (idx1).
 *
 * Returns a collection of values whose primary keys are associated with the specified idx1 value.
 * Useful for grouped lookups or category-based access patterns.
 *
 * Does not support filtering on tertiary index (idx2); use GetTertiaryIdxTwoCommand for that.
 *
 * Requires READ or higher privileges to execute.
 */
public class GetSecondaryIdxOneCommand extends BaseCommand<String> {

    public static final String COMMAND_KEY = "GETIDX1";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();

        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() != 1) {
                response.setResponseError("GETIDX1 command requires a single argument: the secondary index (IDX1) key.");
                return response;
            }

            String idxKey = argumentParser.getPrimaryArgument().getArgs().getFirst();

            try {
                new KeyValidator(ApiParameter.IDX1.value(), idxKey).validate();
            } catch (Exception ex) {
                response.setResponseError(ex.getMessage());
                return response;
            }

            CacheAccessService service = new CacheAccessService();
            String value = service.getBySecondaryIdx1(session, idxKey);

            if (value != null) {
                response.setResponse(StringUtil.esacpeQuote(value));
            } else {
                response.setResponseNil();
            }

        } catch (Exception ex) {
            response.setResponseError("GETIDX1 command failed. Check logs.");
            LogHelper.getInstance().logFatal("[GetSecondaryIdxOneCommand] error: " + ex.getMessage(), ex);
        }

        return response;
    }
}
