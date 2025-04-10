package com.vertexcache.core.command;

import com.vertexcache.core.command.impl.*;

import java.util.Map;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

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
    }

    public Command<String> getCommand(String commandName) {
        return commandMap.getOrDefault(commandName, new UnknownCommand());
    }
}