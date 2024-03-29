package com.vertexcache.cli;

import com.vertexcache.cli.console.ConsoleTerminal;

public class VertexCacheConsole {

    public static void main(String[] args) throws Exception {
        System.out.println("test");

        ConsoleTerminal consoleTerminal = new ConsoleTerminal();
        consoleTerminal.execute();
    }

}
