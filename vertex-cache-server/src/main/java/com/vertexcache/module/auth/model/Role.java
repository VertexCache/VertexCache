package com.vertexcache.module.auth.model;

import com.vertexcache.core.command.impl.*;
import com.vertexcache.core.command.impl.admin.ConfigCommand;
import com.vertexcache.core.command.impl.admin.PurgeCommand;
import com.vertexcache.core.command.impl.admin.ResetCommand;
import com.vertexcache.core.command.impl.admin.SessionsCommand;
import com.vertexcache.core.command.impl.internal.RoleChangeCommand;

import java.util.Set;

public enum Role {

    // TCP Roles
    ADMIN,
    READ_ONLY,
    READ_WRITE,

    // REST API Roles
    REST_API_ADMIN,
    REST_API_READ_ONLY,
    REST_API_READ_WRITE,

    // Alert - Webhook
    ALERT_BOT_READ_ONLY,

    // M2M - Cluster Node to Node
    NODE
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

            // Note the Rest Handlers map to the same Command Keys from the respective Commands
            case REST_API_ADMIN -> Set.of(
                    PingCommand.COMMAND_KEY,
                    GetCommand.COMMAND_KEY,
                    GetSecondaryIdxOneCommand.COMMAND_KEY,
                    GetSecondaryIdxTwoCommand.COMMAND_KEY,
                    SetCommand.COMMAND_KEY,
                    DelCommand.COMMAND_KEY,
                    PurgeCommand.COMMAND_KEY,
                    ResetCommand.COMMAND_KEY
            ).contains(command.toUpperCase());
            case REST_API_READ_WRITE -> Set.of(
                    GetCommand.COMMAND_KEY,
                    SetCommand.COMMAND_KEY,
                    DelCommand.COMMAND_KEY
            ).contains(command.toUpperCase());
            case REST_API_READ_ONLY -> Set.of(
                    GetCommand.COMMAND_KEY
            ).contains(command.toUpperCase());

            case ALERT_BOT_READ_ONLY -> false;
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
