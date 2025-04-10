package com.vertexcache.core.command.argument;

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

    public void setArgs(List<String> args) { this.args = args;}

    public boolean isArgsExist() {
        return !args.isEmpty();
    }
}