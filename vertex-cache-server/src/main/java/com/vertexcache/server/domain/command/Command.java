package com.vertexcache.server.domain.command;

import com.vertexcache.server.domain.command.argument.ArgumentParser;

public interface Command<T> {
    CommandResponse execute(ArgumentParser argumentParser);
}
