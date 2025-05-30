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
package com.vertexcache.core.setting.loaders;

import com.vertexcache.core.setting.ConfigKey;
import com.vertexcache.core.setting.model.LoaderBase;

/**
 * Lightweight configuration loader that determines global enablement of ADMIN command access.
 *
 * Acts as a system-wide override that blocks all ADMIN-level command execution,
 * regardless of client-assigned roles or authentication status.
 *
 * If disabled, all incoming ADMIN commands (e.g., PURGE, RESET, SHUTDOWN) will be rejected,
 * even for clients with ADMIN privileges.
 *
 * Intended as a security safeguard to enforce strict operational boundaries,
 * especially in shared or production environments.
 *
 * @see com.vertexcache.core.command.impl.admin.AdminCommand how it overrides the Admin related commands
 */
public class AdminConfigLoader extends LoaderBase {

    private boolean enableAdminCommands;

    @Override
    public void load() {
        this.enableAdminCommands = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_ADMIN_COMMANDS,ConfigKey.ENABLE_ADMIN_COMMANDS_DEFAULT);
    }

    public boolean isAdminCommandsEnabled() { return enableAdminCommands; }
    public void setEnableAdminCommands(boolean enableAdminCommands) { this.enableAdminCommands = enableAdminCommands;}
}
