package com.vertexcache.core.command;

public abstract class BaseCommand<T> implements Command<T> {

    protected abstract String getCommandKey(); // required by subclass

    @Override
    public String getCommandName() {
        return getCommandKey();
    }
}
