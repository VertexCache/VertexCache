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
import com.vertexcache.core.cache.CacheAccessService;
import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.validation.validators.KeyValidator;
import com.vertexcache.module.restapi.model.ApiParameter;
import com.vertexcache.server.session.ClientSessionContext;

/**
 * Command used to delete a key and its associated value from the cache.
 *
 * Removes the primary key and, if applicable, cleans up any secondary or
 * tertiary index references associated with the entry.
 *
 * This operation is irreversible and will evict the entry immediately.
 * Requires READ_WRITE or higher privileges to execute.
 */
public class DelCommand extends BaseCommand<String> {

    public static final String COMMAND_KEY = "DEL";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();

        try {
            if (argumentParser.getPrimaryArgument().getArgs().size() != 1) {
                response.setResponseError("DEL command requires a single argument: the key to remove.");
                return response;
            }

            String key = argumentParser.getPrimaryArgument().getArgs().getFirst();

            try {
                new KeyValidator(ApiParameter.KEY.value(), key).validate();
            } catch (Exception ex) {
                response.setResponseError(ex.getMessage());
                return response;
            }

            CacheAccessService service = new CacheAccessService();
            service.remove(session, key);
            response.setResponseOK();

        } catch (Exception ex) {
            response.setResponseError("DEL command failed. Check logs.");
            LogHelper.getInstance().logFatal("[DelCommand] error: " + ex.getMessage(), ex);
        }

        return response;
    }
}
