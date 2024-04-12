package com.vertexcache.cli;

import com.vertexcache.cli.console.ConsoleTerminal;
import com.vertexcache.cli.domain.config.Config;
import com.vertexcache.common.cli.CommandLineArgsParser;

public class VertexCacheConsole {

    public static void main(String[] args) throws Exception {
       // Config.getInstance().loadPropertiesFromArgs(new CommandLineArgsParser(args));
        ConsoleTerminal consoleTerminal = new ConsoleTerminal();
        consoleTerminal.execute();
    }

}
