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

import com.vertexcache.sdk.protocol.command.*;
import com.vertexcache.sdk.result.CommandResult;
import com.vertexcache.sdk.result.GetResult;
import com.vertexcache.sdk.setting.ClientOption;
import com.vertexcache.sdk.transport.TcpClient;
import com.vertexcache.sdk.transport.TcpClientInterface;

import java.util.List;

public class VertexCacheSDK {

    private ClientOption clientOption;
    private TcpClientInterface tcpClient;

    public VertexCacheSDK(ClientOption clientOption) {
        this.clientOption = clientOption;

        this.tcpClient = new TcpClient(
                clientOption.getServerHost(),
                clientOption.getServerPort(),
                clientOption.isEnableTlsEncryption(),
                clientOption.isVerifyCertificate(),
                clientOption.getTlsCertificate(),
                clientOption.getConnectTimeout(),
                clientOption.getReadTimeout(),
                clientOption.getEncryptionMode(),
                clientOption.getPublicKey(),
                clientOption.getSharedEncryptionKey(),
                clientOption.getClientId(),
                clientOption.getClientToken()
        );
    }

    protected VertexCacheSDK (ClientOption clientOption, TcpClientInterface tcpClient) {
        this.clientOption = clientOption;
        this.tcpClient = tcpClient;
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
        DelCommand cmd = (DelCommand) DelCommand.of(key).execute(tcpClient);
        return new CommandResult(cmd.isSuccess(), cmd.getStatusMessage());
    }

    public CommandResult del(List<String> keys) {
        DelCommand cmd = (DelCommand) new DelCommand(keys).execute(tcpClient);
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
