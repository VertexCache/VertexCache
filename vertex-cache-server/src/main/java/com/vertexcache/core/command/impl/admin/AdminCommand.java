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

import com.vertexcache.core.command.BaseCommand;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleName;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.server.session.ClientSessionContext;

public abstract class AdminCommand<T> extends BaseCommand<T> {

    @Override
    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session)  {

        if (!isAdminAccessAllowed())
            return rejectIfAdminAccessNotAllowed();

        return this.executeAdminCommand(argumentParser,session);
    }

    abstract CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session);

    protected boolean isAdminAccessAllowed() {
        return isAuthModuleEnabled() && isAdminModuleEnabled();
    }

    private boolean isAuthModuleEnabled() {
        return ModuleRegistry.getInstance()
                .getModuleByEnum(ModuleName.AUTH)
                .filter(m -> m instanceof Module mod && mod.getModuleStatus() != ModuleStatus.DISABLED)
                .isPresent();
    }

    private boolean isAdminModuleEnabled() {
        return ModuleRegistry.getInstance()
                .getModuleByEnum(ModuleName.ADMIN)
                .filter(m -> m instanceof Module mod && mod.getModuleStatus() != ModuleStatus.DISABLED)
                .isPresent();
    }

    protected CommandResponse rejectIfAdminAccessNotAllowed() {
        CommandResponse response = new CommandResponse();
        response.setResponseError("ERR Admin access denied, Admin and/or Auth module is not enabled.");
        return response;
    }
}
