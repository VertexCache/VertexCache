package com.vertexcache.domain.command;

import com.vertexcache.domain.command.impl.GetCommand;
import com.vertexcache.domain.command.impl.PingCommand;
import com.vertexcache.domain.command.impl.SetCommand;
import com.vertexcache.domain.command.impl.UnknownCommand;

import java.util.Map;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

public class CommandFactory {
    private final Map<String, Command<String>> commandMap;

    public CommandFactory() {
        commandMap = new CaseInsensitiveMap<>();
        commandMap.put(PingCommand.COMMAND_KEY, new PingCommand());
        commandMap.put(GetCommand.COMMAND_KEY, new GetCommand());
        commandMap.put(SetCommand.COMMAND_KEY, new SetCommand());
    }

    public Command<String> getCommand(String commandName) {
        return commandMap.getOrDefault(commandName, new UnknownCommand());
    }
}