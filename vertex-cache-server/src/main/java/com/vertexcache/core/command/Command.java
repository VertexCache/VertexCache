package com.vertexcache.core.command;

import com.vertexcache.core.command.argument.ArgumentParser;

public interface Command<T> {
    CommandResponse execute(ArgumentParser argumentParser);
    String getCommandName();
}
