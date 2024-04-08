package com.vertexcache.domain.command;

import com.vertexcache.domain.command.argument.ArgumentParser;

public interface Command<T> {
    //CommandResponse execute(T... args);
    CommandResponse execute(ArgumentParser argumentParser);
}
