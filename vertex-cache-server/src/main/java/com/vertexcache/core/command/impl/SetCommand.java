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
import com.vertexcache.core.validation.ValidationBatch;
import com.vertexcache.core.validation.validators.KeyValidator;
import com.vertexcache.module.restapi.model.ApiParameter;
import com.vertexcache.server.session.ClientSessionContext;

import java.util.ArrayList;

/**
 * Command used to store or update a key-value entry in the cache.
 *
 * Supports optional indexing via secondary (idx1) and tertiary (idx2) keys,
 * allowing for advanced lookup capabilities beyond the primary key.
 *
 * Overwrites any existing value for the same key.
 *
 * Requires READ_WRITE or higher privileges to execute.
 *
 */
public class SetCommand extends BaseCommand<String> {

    private static final String SUB_ARG_SECONDARY_INDEX_ONE = "IDX1";
    private static final String SUB_ARG_SECONDARY_INDEX_TWO = "IDX2";

    public static final String COMMAND_KEY = "SET";
    private final ArrayList<String> subArguments;

    public SetCommand() {
        this.subArguments = new ArrayList<>();
        this.subArguments.add(SUB_ARG_SECONDARY_INDEX_ONE);
        this.subArguments.add(SUB_ARG_SECONDARY_INDEX_TWO);
    }

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();

        try {
            argumentParser.setSubArguments(this.subArguments);

            var args = argumentParser.getPrimaryArgument().getArgs();
            if (args.size() != 2) {
                response.setResponseError("SET requires two arguments: key-name and key-value [IDX1] <optional-index-1> [IDX2] <optional-index-2>");
                return response;
            }

            String key = args.get(0);
            String value = args.get(1).replace("\"", "\\\"");

            CacheAccessService service = new CacheAccessService();

            boolean hasIdx1 = argumentParser.subArgumentExists(SUB_ARG_SECONDARY_INDEX_ONE)
                    && argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().size() == 1;
            boolean hasIdx2 = argumentParser.subArgumentExists(SUB_ARG_SECONDARY_INDEX_TWO)
                    && argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_TWO).getArgs().size() == 1;



            if (hasIdx1 && hasIdx2) {
                String idx1 = argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().getFirst();
                String idx2 = argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_TWO).getArgs().getFirst();

                ValidationBatch batch = new ValidationBatch();
                batch.check(ApiParameter.KEY.value(), new KeyValidator(ApiParameter.KEY.value(), key));
                batch.check(ApiParameter.IDX1.value(), new KeyValidator(ApiParameter.IDX1.value(), idx1));
                batch.check(ApiParameter.IDX2.value(), new KeyValidator(ApiParameter.IDX2.value(), idx2));

                if(batch.hasErrors()) {
                    response.setResponseError(batch.getSummary());
                    return response;
                }

                service.put(session, key, value, idx1, idx2);
            } else if (hasIdx1) {
                String idx1 = argumentParser.getSubArgumentByName(SUB_ARG_SECONDARY_INDEX_ONE).getArgs().getFirst();

                ValidationBatch batch = new ValidationBatch();
                batch.check(ApiParameter.KEY.value(), new KeyValidator(ApiParameter.KEY.value(), key));
                batch.check(ApiParameter.IDX1.value(), new KeyValidator(ApiParameter.IDX1.value(), idx1));

                if(batch.hasErrors()) {
                    response.setResponseError(batch.getSummary());
                    return response;
                }

                service.put(session, key, value, idx1);
            } else {
                try {
                    new KeyValidator(ApiParameter.KEY.value(), key).validate();
                } catch (Exception ex) {
                    response.setResponseError(ex.getMessage());
                    return response;
                }
                service.put(session, key, value);
            }

            response.setResponseOK();

        } catch (Exception ex) {
            response.setResponseError("SET command failed. Check logs.");
            LogHelper.getInstance().logFatal("[SetCommand] error: " + ex.getMessage(), ex);
        }

        return response;
    }
}
