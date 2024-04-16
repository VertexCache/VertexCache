package com.vertexcache.console;

import com.vertexcache.console.domain.terminal.ConsoleTerminal;
import com.vertexcache.console.domain.config.Config;
import com.vertexcache.common.cli.CommandLineArgsParser;

public class VertexCacheConsole {

    public static void main(String[] args) throws Exception {
        Config.getInstance().loadPropertiesFromArgs(new CommandLineArgsParser(args));
        ConsoleTerminal consoleTerminal = new ConsoleTerminal();
        consoleTerminal.execute();
    }

}
