package com.vertexcache.core.command;

import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.core.command.impl.UnknownCommand;

public class CommandService {

    private CommandFactory commandFactory = new CommandFactory();

    public byte[] execute(byte[] requestAsBytes) {
        if (requestAsBytes != null && requestAsBytes.length > 0) {
            ArgumentParser argumentParser = new ArgumentParser(new String(requestAsBytes));
            Command<String> command = commandFactory.getCommand(argumentParser.getPrimaryArgument().getName());
            return command.execute(argumentParser).toVCMPAsBytes();
        }
        return (new UnknownCommand()).execute().toVCMPAsBytes();
    }
}
