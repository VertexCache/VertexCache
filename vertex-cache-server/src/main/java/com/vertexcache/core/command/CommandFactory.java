package com.vertexcache.core.command;

import com.vertexcache.core.command.impl.*;

import java.util.Map;

import com.vertexcache.core.command.impl.admin.ReloadCommand;
import com.vertexcache.core.command.impl.admin.ShutdownCommand;
import com.vertexcache.core.command.impl.admin.StatusCommand;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

/**
 * Register Commands, note commands are not entirely accessible to everyone, if Auth is enabled, then client
 * role matters
 *
 * Manual at the moment, maybe revisit if list of commands get unfeasible to manage to dynamically load
 * but do need to consider performance and do want added behaviour for disabling a command at
 * run-time - Command Registry similar to the ModuleRegistry
 *
 * @see com.vertexcache.module.auth.Role
 */
public class CommandFactory {
    private final Map<String, Command<String>> commandMap;

    public CommandFactory() {
        commandMap = new CaseInsensitiveMap<>();

        commandMap.put(PingCommand.COMMAND_KEY, new PingCommand());

        commandMap.put(GetCommand.COMMAND_KEY, new GetCommand());
        commandMap.put(GetSecondaryIdxOneCommand.COMMAND_KEY, new GetSecondaryIdxOneCommand());
        commandMap.put(GetSecondaryIdxTwoCommand.COMMAND_KEY, new GetSecondaryIdxTwoCommand());

        commandMap.put(SetCommand.COMMAND_KEY, new SetCommand());
        commandMap.put(DelCommand.COMMAND_KEY, new DelCommand());

        // Intended for Admin Only
        commandMap.put(StatusCommand.COMMAND_KEY, new StatusCommand());
        commandMap.put(ShutdownCommand.COMMAND_KEY, new ShutdownCommand());
        commandMap.put(ReloadCommand.COMMAND_KEY, new ReloadCommand());
    }

    public Command<String> getCommand(String commandName) {
        return commandMap.getOrDefault(commandName, new UnknownCommand());
    }
}