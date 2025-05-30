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

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.setting.Config;
import com.vertexcache.server.session.ClientSessionContext;

/**
 * Administrative command that triggers a configuration reload from disk.
 *
 * Reloads the active configuration file (e.g., .env or system properties)
 * without restarting the server. This enables live updates to settings that
 * support dynamic reloading.
 *
 * Requires ADMIN privileges to execute.
 *
 * Common use cases:
 * - Applying updated configuration after manual edits
 * - Syncing environment-based changes across cluster nodes
 */
public class ReloadCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "RELOAD";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();

        try {
            // Reload config and modules
            Config.getInstance().reloadFromDisk();
            ModuleRegistry.getInstance().stopModules();
            ModuleRegistry.getInstance().loadModules();

            response.setResponse("OK: Configuration and modules reloaded.");
        } catch (Exception e) {
            response.setResponseError("ERR_RELOAD_FAILED Failed to reload configuration: " + e.getMessage());
        }

        return response;
    }
}
