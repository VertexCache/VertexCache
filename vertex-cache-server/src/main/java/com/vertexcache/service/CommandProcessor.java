package com.vertexcache.service;

import com.vertexcache.common.protocol.VertexCacheMessageProtocol;
import com.vertexcache.service.command.UnknownCommand;
import org.apache.logging.log4j.core.util.ArrayUtils;

public class CommandProcessor {

    private CommandFactory commandFactory = new CommandFactory();
    private CommandInvoker commandInvoker = new CommandInvoker(commandFactory);

    public byte[] execute(byte[] requestAsBytes) {

        if (requestAsBytes != null && requestAsBytes.length > 0) {

            String[] request = new String(requestAsBytes).toLowerCase().split("\\s+");

            return VertexCacheMessageProtocol.encodeString(commandInvoker.execute(request[0], ArrayUtils.remove(request, 0)));
        }
        return VertexCacheMessageProtocol.encodeError((new UnknownCommand()).execute());
    }
}
