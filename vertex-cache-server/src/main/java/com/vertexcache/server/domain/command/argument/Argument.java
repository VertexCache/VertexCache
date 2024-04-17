package com.vertexcache.server.domain.command.argument;

import java.util.List;

public class Argument {
    private String name;
    private List<String> args;

    public Argument(String name, List<String> args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public List<String> getArgs() {
        return args;
    }

    public boolean isArgsExist() {
        return !args.isEmpty();
    }
}