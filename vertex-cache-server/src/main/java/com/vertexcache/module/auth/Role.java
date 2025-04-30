package com.vertexcache.module.auth;

import com.vertexcache.core.command.impl.*;
import com.vertexcache.core.command.impl.internal.RoleChangeCommand;

import java.util.Set;

public enum Role {
    READ_ONLY,
    READ_WRITE,
    ADMIN,
    NODE // this for M2M only, ie: Clustering
    ;

    public boolean canExecute(String command) {
        return switch (this) {
            case ADMIN -> true;
            case READ_WRITE ->
                    Set.of(
                            PingCommand.COMMAND_KEY,
                            GetCommand.COMMAND_KEY,
                            GetSecondaryIdxOneCommand.COMMAND_KEY,
                            GetSecondaryIdxTwoCommand.COMMAND_KEY,
                            SetCommand.COMMAND_KEY,
                            DelCommand.COMMAND_KEY

                    ).contains(command.toUpperCase());
            case READ_ONLY -> Set.of(
                            PingCommand.COMMAND_KEY,
                            GetCommand.COMMAND_KEY,
                            GetSecondaryIdxOneCommand.COMMAND_KEY,
                            GetSecondaryIdxTwoCommand.COMMAND_KEY
                    ).contains(command.toUpperCase());
            case NODE -> Set.of(
                            RoleChangeCommand.COMMAND_KEY
                    ).contains(command.toUpperCase());
        };
    }

    public static Set<String> allowedCommands(Role role) {
        return switch (role) {
            case ADMIN -> Set.of("*");
            case READ_WRITE -> Set.of(
                        PingCommand.COMMAND_KEY,
                        GetCommand.COMMAND_KEY,
                        GetSecondaryIdxOneCommand.COMMAND_KEY,
                        GetSecondaryIdxTwoCommand.COMMAND_KEY,
                        SetCommand.COMMAND_KEY,
                        DelCommand.COMMAND_KEY
                    );
            case READ_ONLY -> Set.of(
                        PingCommand.COMMAND_KEY,
                        GetCommand.COMMAND_KEY,
                        GetSecondaryIdxOneCommand.COMMAND_KEY,
                        GetSecondaryIdxTwoCommand.COMMAND_KEY
                    );
            case NODE -> Set.of(
                        RoleChangeCommand.COMMAND_KEY
                    );
        };
    }

    public static Role fromString(String input) {
        if (input == null) return READ_ONLY;
        try {
            return Role.valueOf(input.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return READ_ONLY;
        }
    }

}
