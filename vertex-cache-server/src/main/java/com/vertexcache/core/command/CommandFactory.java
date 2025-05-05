package com.vertexcache.core.command;

import com.vertexcache.core.command.impl.*;

import java.util.Map;

import com.vertexcache.core.command.impl.admin.*;
import com.vertexcache.core.command.impl.internal.PeerPingCommand;
import com.vertexcache.core.command.impl.internal.RoleChangeCommand;
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

        commandMap.put(ClusterStatusCommand.COMMAND_KEY, new ClusterStatusCommand());

        // Intended for Admin Only
        commandMap.put(StatusCommand.COMMAND_KEY, new StatusCommand());
        commandMap.put(ShutdownCommand.COMMAND_KEY, new ShutdownCommand());
        commandMap.put(ReloadCommand.COMMAND_KEY, new ReloadCommand());
        commandMap.put(ConfigCommand.COMMAND_KEY, new ConfigCommand());
        commandMap.put(ResetCommand.COMMAND_KEY, new ResetCommand());
        commandMap.put(SessionsCommand.COMMAND_KEY, new SessionsCommand());
        commandMap.put(PurgeCommand.COMMAND_KEY, new PurgeCommand());
        commandMap.put(MetricsCommand.COMMAND_KEY, new MetricsCommand());

        // Intended for Internal M2M / Clustering
        commandMap.put(RoleChangeCommand.COMMAND_KEY, new RoleChangeCommand());
        commandMap.put(PeerPingCommand.COMMAND_KEY, new PeerPingCommand());
    }

    public Command<String> getCommand(String commandName) {
        return commandMap.getOrDefault(commandName, new UnknownCommand());
    }
}