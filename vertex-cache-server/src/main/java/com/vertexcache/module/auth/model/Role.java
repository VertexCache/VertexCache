/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.module.auth.model;

import com.vertexcache.core.command.impl.*;
import com.vertexcache.core.command.impl.admin.ConfigCommand;
import com.vertexcache.core.command.impl.admin.PurgeCommand;
import com.vertexcache.core.command.impl.admin.ResetCommand;
import com.vertexcache.core.command.impl.admin.SessionsCommand;
import com.vertexcache.core.command.impl.internal.PeerPingCommand;
import com.vertexcache.core.command.impl.internal.RoleChangeCommand;

import java.util.Set;

public enum Role {

    // TCP Roles
    ADMIN,
    READ_ONLY,
    READ_WRITE,

    // REST API Roles
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
                            PeerPingCommand.COMMAND_KEY,
                            RoleChangeCommand.COMMAND_KEY
                    ).contains(command.toUpperCase());

            // Note the Rest Handlers map to the same Command Keys from the respective Commands
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
