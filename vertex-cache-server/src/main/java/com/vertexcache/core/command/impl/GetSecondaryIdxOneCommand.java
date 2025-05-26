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
import com.vertexcache.server.session.ClientSessionContext;

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
