package com.vertexcache.sdk.protocol.command;

import com.vertexcache.sdk.protocol.BaseCommand;
import com.vertexcache.sdk.protocol.CommandType;

public class PingCommand extends BaseCommand {

    @Override
    protected String buildCommand() {
        return CommandType.PING.toString();
    }
}
