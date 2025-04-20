package com.vertexcache.core.command;

public abstract class BaseCommand<T> implements Command<T> {

    protected abstract String getCommandKey(); // required by subclass

    protected static final String COMMAND_PRETTY = "PRETTY";

    @Override
    public String getCommandName() {
        return getCommandKey();
    }
}
