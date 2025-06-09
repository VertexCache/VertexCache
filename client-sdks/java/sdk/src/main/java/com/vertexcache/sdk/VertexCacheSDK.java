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
package com.vertexcache.sdk;

import com.vertexcache.sdk.comm.ClientConnector;
import com.vertexcache.sdk.comm.ClientConnectorInterface;
import com.vertexcache.sdk.command.impl.*;
import com.vertexcache.sdk.model.CommandResult;
import com.vertexcache.sdk.model.GetResult;
import com.vertexcache.sdk.model.ClientOption;

/**
 * VertexCacheSDK serves as the main entry point for interacting with the VertexCache server.
 * It provides methods to perform cache operations such as GET, SET, and DEL, and abstracts away
 * the underlying TCP transport details.
 *
 * This SDK handles encryption (symmetric/asymmetric), TLS negotiation, authentication, and framing
 * of commands and responses. Errors are surfaced through structured exceptions to aid client integration.
 */
public class VertexCacheSDK {

    private ClientConnectorInterface tcpClient;

    public VertexCacheSDK(ClientOption clientOption) {
        this.tcpClient = new ClientConnector(clientOption);
    }

    public void openConnection() {
       this.tcpClient.connect();
    }

    public CommandResult ping() {
        PingCommand cmd = (PingCommand) new PingCommand().execute(tcpClient);
        return new CommandResult(cmd.isSuccess(), cmd.getStatusMessage());
    }

    public CommandResult set(String key, String value) {
        SetCommand cmd = (SetCommand) new SetCommand(key, value).execute(tcpClient);
        return new CommandResult(cmd.isSuccess(), cmd.getStatusMessage());
    }

    public CommandResult set(String key, String value, String secondaryIndexKey) {
        SetCommand cmd = (SetCommand) new SetCommand(key, value,secondaryIndexKey).execute(tcpClient);
        return new CommandResult(cmd.isSuccess(), cmd.getStatusMessage());
    }

    public CommandResult set(String key, String value, String secondaryIndexKey, String tertiaryIndexKey) {
        SetCommand cmd = (SetCommand) new SetCommand(key, value, secondaryIndexKey, tertiaryIndexKey).execute(tcpClient);
        return new CommandResult(cmd.isSuccess(), cmd.getStatusMessage());
    }

    public CommandResult del(String key) {
        DelCommand cmd = (DelCommand) new DelCommand(key).execute(tcpClient);
        return new CommandResult(cmd.isSuccess(), cmd.getStatusMessage());
    }

    public GetResult get(String key) {
        GetCommand cmd = (GetCommand) new GetCommand(key).execute(tcpClient);
        return new GetResult(cmd.isSuccess(), cmd.getStatusMessage(), cmd.getValue());
    }

    public GetResult getBySecondaryIndex(String key) {
        GetSecondaryIdxOneCommand cmd = (GetSecondaryIdxOneCommand) new GetSecondaryIdxOneCommand(key).execute(tcpClient);
        return new GetResult(cmd.isSuccess(), cmd.getStatusMessage(), cmd.getValue());
    }

    public GetResult getByTertiaryIndex(String key) {
        GetSecondaryIdxTwoCommand cmd = (GetSecondaryIdxTwoCommand) new GetSecondaryIdxTwoCommand(key).execute(tcpClient);
        return new GetResult(cmd.isSuccess(), cmd.getStatusMessage(), cmd.getValue());
    }

    public boolean isConnected() {
        return tcpClient.isConnected();
    }

    public void close() {
        tcpClient.close();
    }
}
