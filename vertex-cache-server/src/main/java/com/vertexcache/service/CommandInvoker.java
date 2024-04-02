package com.vertexcache.service;

public class CommandInvoker {
    private final CommandFactory commandFactory;

    public CommandInvoker(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public String execute(String commandName, String... args) {
        Command<String> command = commandFactory.getCommand(commandName);
        if (command == null) {
            throw new IllegalArgumentException("Unknown command: " + commandName);
        }
        return command.execute(args);
    }
}
