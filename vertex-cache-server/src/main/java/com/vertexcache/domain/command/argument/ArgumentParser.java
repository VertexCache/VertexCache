package com.vertexcache.domain.command.argument;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArgumentParser {

    private String argumentString;
    private Set<String> subArguments;
    private List<Argument> arguments = new ArrayList<>();

    public ArgumentParser(String argumentString) {
        if(argumentString != null && !argumentString.isEmpty()) {
            this.argumentString = argumentString.trim();
        }
        this.subArguments = new HashSet<>(); // initialized empty
        this.parseArguments();
    }

    public void setSubArguments(Set<String> subArguments) {
        this.arguments = new ArrayList<>(); // reset
        this.subArguments = subArguments;
        this.parseArguments();
    }

    public Argument getPrimaryArgument() {
        if(arguments != null && arguments.size() > 0) {
            return arguments.getFirst();
        }
        return null;
    }

    private void parseArguments() {
        String[] parts = this.argumentString.split("\\s+");
        int i = 0;

        while (i < parts.length) {
            String name = parts[i++];
            List<String> args = new ArrayList<>();

            if(parts.length == 1) {
                this.arguments.add(new Argument(name, args));
            } else {

                while (i < parts.length && !parts[i].matches("[A-Z]+") && !this.subArguments.contains(parts[i])) {
                    args.add(parts[i++]);
                    this.arguments.add(new Argument(name, args));
                }

                if (i < parts.length && this.subArguments.contains(parts[i])) {
                    List<String> subArgumentArgs = new ArrayList<>();
                    String subArgumentName = parts[i++];

                    while (i < parts.length && !parts[i].matches("[A-Z]+") && !this.subArguments.contains(parts[i])) {
                        subArgumentArgs.add(parts[i++]);
                    }

                    this.arguments.add(new Argument(subArgumentName, subArgumentArgs));
                } //else {
                    //  this.arguments.add(new Argument(name, args));
                //}
            }
        }
    }

    public Argument getSubArgumentByName(String subArgumentName) {
        for (Argument Argument : this.arguments) {
            if (Argument.getName().equals(subArgumentName)) {
                return Argument;
            }
        }
        return null;
    }

    public boolean subArgumentExists(String subArgumentName) {
        return getSubArgumentByName(subArgumentName) != null;
    }

    public boolean isArgumentsExists() {
        return !this.arguments.isEmpty();
    }
}
