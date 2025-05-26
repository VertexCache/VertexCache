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
package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.cache.Cache;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.setting.Config;
import com.vertexcache.server.session.ClientSessionContext;

import java.util.Set;
import java.util.stream.Collectors;

public class PurgeCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "PURGE";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();

        if (argumentParser.getPrimaryArgument().getArgs().isEmpty()) {
            response.setResponseError("PURGE command requires a key or prefix argument. Usage: PURGE <key or prefix>");
            return response;
        }

        String inputPrefix = argumentParser.getPrimaryArgument().getArgs().getFirst();
        String targetPrefix;

        if (Config.getInstance().getAuthWithTenantConfigLoader().isTenantKeyPrefixingEnabled()) {
            targetPrefix = session.getTenantId() + "::" + inputPrefix;
        } else {
            targetPrefix = inputPrefix;
        }

        try {
            Set<String> matches = Cache.getInstance().keySet().stream()
                    .filter(k -> k.startsWith(targetPrefix))
                    .collect(Collectors.toSet());

            if (matches.isEmpty()) {
                response.setResponse("OK: No keys matched the prefix '" + inputPrefix + "'.");
                return response;
            }

            matches.forEach(Cache.getInstance()::remove);
            response.setResponse("OK: Purged " + matches.size() + " key(s) matching prefix '" + inputPrefix + "'.");

        } catch (Exception e) {
            response.setResponseError("ERR_PURGE Cache operation failed: " + e.getMessage());
        }

        return response;
    }
}
