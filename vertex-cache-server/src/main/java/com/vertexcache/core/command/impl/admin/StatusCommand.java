package com.vertexcache.core.command.impl.admin;

import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleRegistry;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.server.session.ClientSessionContext;

public class StatusCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "STATUS";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) {
        if (!isAdminAccessAllowed()) return rejectIfAdminAccessNotAllowed();

        CommandResponse response = new CommandResponse();
        StringBuilder sb = new StringBuilder("Modules:\n");

        ModuleRegistry.getInstance().getAllModules().forEach((name, handler) -> {
            sb.append("  ").append(name).append(": ");

            if (handler instanceof Module module) {
                ModuleStatus status = module.getModuleStatus();
                String runtimeStatus = module.getStatusSummary();
                String message = module.getStatusMessage();

                sb.append(status);
                if (!runtimeStatus.isBlank()) sb.append(" | ").append(runtimeStatus);
                if (!message.isBlank()) sb.append(" | ").append(message);
            } else {
                sb.append("Non-standard module handler");
            }

            sb.append("\n");
        });

        response.setResponse(sb.toString().trim());
        return response;
    }
}
