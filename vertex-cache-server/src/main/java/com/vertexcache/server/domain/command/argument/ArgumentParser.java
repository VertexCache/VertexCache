package com.vertexcache.server.domain.command.argument;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentParser {

    private String argumentString;
    private String[] parts;
    private ArrayList<String> subArguments;
    private List<Argument> arguments = new ArrayList<>();

    public ArgumentParser(String argumentString) {
        if(argumentString != null && !argumentString.isEmpty()) {
            this.argumentString = argumentString.trim();
            this.parts = this.splitWithQuotes(this.argumentString);
        }
        this.subArguments = new ArrayList<>(); // initialized empty
        this.parseArguments();
    }

    public void setSubArguments(ArrayList<String> subArguments) {
        this.arguments = new ArrayList<>(); // reset
        this.subArguments = subArguments;
        this.parseArguments();
    }

    /* No Args, just the cmd */
    public Argument getPrimaryArgument() {
        return arguments.getFirst();
    }

    private void parseArguments() {

        if(parts == null || parts.length == 0) {
            return;
        }

        if(parts.length == 1) {
            // Only one cmd, ie: PING
            this.arguments.add(new Argument(parts[0], new ArrayList<>()));
        } else {
            this.arguments.add( new Argument(parts[0],new ArrayList<>()));
            List<String> args = new ArrayList<>();

            for (int i = 1; i < parts.length; i++) {
                if(this.isSubArgument(parts[i])) {
                    if(this.arguments.size() == 1) {
                        // Primary Cmd, 1st argument
                        this.arguments.getFirst().setArgs(args);
                        args = new ArrayList<>();

                        // Create next argument
                        this.arguments.add( new Argument(parts[i],new ArrayList<>()));
                    } else {
                        this.arguments.get(this.arguments.size() - 1).setArgs(args);
                        args = new ArrayList<>();
                        this.arguments.add( new Argument(parts[i],new ArrayList<>()));
                    }
                } else {
                    args.add(parts[i]);
                }

            }
            this.arguments.get(this.arguments.size() - 1).setArgs(args);
        }
    }

    private String[] splitWithQuotesold1(String argumentString) {
        List<String> parts = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(argumentString);
        while (m.find()) {
            parts.add(m.group(1).replaceAll("\"", ""));
        }
        return parts.toArray(new String[0]);
    }

    private String[] splitWithQuotesold2(String argumentString) {
        List<String> parts = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\"[^\"]+\")\\s*").matcher(argumentString);
        while (m.find()) {
            parts.add(m.group(1)); // Do NOT remove quotes
        }
        return parts.toArray(new String[0]);
    }

    private String[] splitWithQuotes(String argumentString) {
        List<String> parts = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\"[^\"]+\")\\s*").matcher(argumentString);
        while (m.find()) {
            String value = m.group(1);
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1); // Remove surrounding quotes
            }
            parts.add(value);
        }
        return parts.toArray(new String[0]);
    }



    public Argument getSubArgumentByName(String subArgumentName) {
        for (Argument argument : this.arguments) {
            if (argument.getName().equalsIgnoreCase(subArgumentName)) {
                return argument;
            }
        }
        return null;
    }

    public boolean subArgumentExists(String subArgumentName) {
        return getSubArgumentByName(subArgumentName.toLowerCase()) != null;
    }

    public boolean isArgumentsExists() {
        return !this.arguments.isEmpty();
    }

    public boolean isSubArgument(String target) {
        for (String element : this.subArguments) {
            if (element.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }
}
