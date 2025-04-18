package com.vertexcache.core.command;

import com.vertexcache.core.command.argument.ArgumentParser;
import com.vertexcache.server.session.ClientSessionContext;

public interface Command<T> {
    CommandResponse execute(ArgumentParser argumentParser, ClientSessionContext session) throws Exception;
    String getCommandName();
}
