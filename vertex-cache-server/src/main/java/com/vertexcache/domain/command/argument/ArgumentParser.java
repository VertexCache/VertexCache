package com.vertexcache.domain.command.argument;

import com.vertexcache.domain.command.argument.Argument;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArgumentParser {

    private String argumentString;
    private Set<String> subArguments;
    private List<Argument> arguments = new ArrayList<>();

    public ArgumentParser(String argumentString) {
        this.argumentString = argumentString;
        this.subArguments = new HashSet<>(); // initialized empty
        this.parseArguments();
    }

    public void setSubArguments(Set<String> subArguments) {
        this.subArguments = subArguments;
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

            while (i < parts.length && !parts[i].matches("[A-Z]+") && !subArguments.contains(parts[i])) {
                args.add(parts[i++]);
            }

            if (i < parts.length && subArguments.contains(parts[i])) {
                List<String> subArgumentArgs = new ArrayList<>();
                String subArgumentName = parts[i++];

                while (i < parts.length && !parts[i].matches("[A-Z]+") && !subArguments.contains(parts[i])) {
                    subArgumentArgs.add(parts[i++]);
                }

                this.arguments.add(new Argument(subArgumentName, subArgumentArgs));
            } else {
                this.arguments.add(new Argument(name, args));
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
}
