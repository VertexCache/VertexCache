package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.setting.Config;
import com.vertexcache.server.session.ClientSessionContext;

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
