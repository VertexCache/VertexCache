package com.vertexcache.core.command.impl.admin;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.core.command.CommandResponse;
import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;

public class ShutdownCommand extends AdminCommand<String> {

    public static final String COMMAND_KEY = "SHUTDOWN";

    @Override
    protected String getCommandKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResponse executeAdminCommand(ArgumentParser argumentParser, ClientSessionContext session) {
        CommandResponse response = new CommandResponse();
        response.setResponse("OK: Shutdown initiated");
        String who = session != null ? session.getClientId() : "unknown";
        LogHelper.getInstance().logInfo("[ADMIN SHUTDOWN] Shutdown triggered by client: " + who);

        // Delay to allow the response to be sent before exit
        new Thread(() -> {
            try {
                Thread.sleep(100); // slight delay
            } catch (InterruptedException ignored) {}
            System.exit(0);
        }).start();

        return response;
    }
}
