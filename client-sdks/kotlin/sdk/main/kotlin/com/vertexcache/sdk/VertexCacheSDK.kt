// ------------------------------------------------------------------------------
// Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// ------------------------------------------------------------------------------
package com.vertexcache.sdk

import com.vertexcache.sdk.comm.ClientConnector
import com.vertexcache.sdk.command.impl.DelCommand
import com.vertexcache.sdk.command.impl.GetCommand
import com.vertexcache.sdk.command.impl.GetSecondaryIdxOneCommand
import com.vertexcache.sdk.command.impl.GetSecondaryIdxTwoCommand
import com.vertexcache.sdk.command.impl.PingCommand
import com.vertexcache.sdk.command.impl.SetCommand
import com.vertexcache.sdk.model.ClientOption
import com.vertexcache.sdk.model.CommandResult
import com.vertexcache.sdk.model.GetResult

/**
 * VertexCacheSDK serves as the main entry point for interacting with the VertexCache server.
 * It provides methods to perform cache operations such as GET, SET, and DEL, and abstracts away
 * the underlying TCP transport details.
 *
 * This SDK handles encryption (symmetric/asymmetric), TLS negotiation, authentication, and framing
 * of commands and responses. Errors are surfaced through structured exceptions to aid client integration.
 */
class VertexCacheSDK(clientOption: ClientOption) {

    private val clientConnector = ClientConnector(clientOption)

    fun openConnection() {
        clientConnector.connect()
    }

    fun ping(): CommandResult {
        val cmd = PingCommand().execute(clientConnector)
        return CommandResult(cmd.isSuccess(), cmd.getStatusMessage() ?: "")
    }

    fun set(key: String, value: String): CommandResult {
        val cmd = SetCommand(key, value).execute(clientConnector)
        return CommandResult(cmd.isSuccess(), cmd.getStatusMessage() ?: "")
    }

    fun set(key: String, value: String, secondaryIndexKey: String): CommandResult {
        val cmd = SetCommand(key, value, secondaryIndexKey).execute(clientConnector)
        return CommandResult(cmd.isSuccess(), cmd.getStatusMessage() ?: "")
    }

    fun set(key: String, value: String, secondaryIndexKey: String, tertiaryIndexKey: String): CommandResult {
        val cmd = SetCommand(key, value, secondaryIndexKey, tertiaryIndexKey).execute(clientConnector)
        return CommandResult(cmd.isSuccess(), cmd.getStatusMessage() ?: "")
    }

    fun del(key: String): CommandResult {
        val cmd = DelCommand(key).execute(clientConnector)
        return CommandResult(cmd.isSuccess(), cmd.getStatusMessage() ?: "")
    }

    fun get(key: String): GetResult {
        val cmd = GetCommand(key).execute(clientConnector)
        return GetResult(cmd.isSuccess(), cmd.getStatusMessage() ?: "", cmd.getValue() ?: "")
    }

    fun getBySecondaryIndex(key: String): GetResult {
        val cmd = GetSecondaryIdxOneCommand(key).execute(clientConnector)
        return GetResult(cmd.isSuccess(), cmd.getStatusMessage() ?: "", cmd.getValue() ?: "")
    }

    fun getByTertiaryIndex(key: String): GetResult {
        val cmd = GetSecondaryIdxTwoCommand(key).execute(clientConnector)
        return GetResult(cmd.isSuccess(), cmd.getStatusMessage() ?: "", cmd.getValue() ?: "")
    }

    fun isConnected(): Boolean {
        return clientConnector.isConnected()
    }

    fun close() {
        clientConnector.close()
    }
}
