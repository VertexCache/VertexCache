package com.vertexcache.service;

import com.vertexcache.service.command.GetCommand;
import com.vertexcache.service.command.PingCommand;
import com.vertexcache.service.command.SetCommand;
import com.vertexcache.service.command.UnknownCommand;

import java.util.Map;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

public class CommandFactory {
    private final Map<String, Command<String>> commandMap;

    public CommandFactory() {
        commandMap = new CaseInsensitiveMap<>();
        commandMap.put(PingCommand.COMMAND_KEY, new PingCommand());
        commandMap.put(GetCommand.COMMAND_KEY, new GetCommand(new CaseInsensitiveMap<>()));
        commandMap.put(SetCommand.COMMAND_KEY, new SetCommand(new CaseInsensitiveMap<>()));
    }

    public Command<String> getCommand(String commandName) {
        return commandMap.getOrDefault(commandName, new UnknownCommand());
    }
}