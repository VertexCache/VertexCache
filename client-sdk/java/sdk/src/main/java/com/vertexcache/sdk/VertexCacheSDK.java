package com.vertexcache.sdk;

import com.vertexcache.sdk.protocol.command.DelCommand;
import com.vertexcache.sdk.protocol.command.GetCommand;
import com.vertexcache.sdk.protocol.command.PingCommand;
import com.vertexcache.sdk.protocol.command.SetCommand;
import com.vertexcache.sdk.result.CommandResult;
import com.vertexcache.sdk.result.GetResult;
import com.vertexcache.sdk.transport.TcpClient;

import java.util.List;

public class VertexCacheSDK {

    private final VertexCacheSDKOptions vertexCacheSDKOptions;
    private final TcpClient tcpClient;

    public VertexCacheSDK(VertexCacheSDKOptions vertexCacheSDKOptions) {
        this.vertexCacheSDKOptions = vertexCacheSDKOptions;

        this.tcpClient = new TcpClient(
                vertexCacheSDKOptions.getServerHost(),
                vertexCacheSDKOptions.getServerPort(),
                vertexCacheSDKOptions.isEnableTlsEncryption(),
                vertexCacheSDKOptions.isVerifyCertificate(),
                vertexCacheSDKOptions.getTlsCertificate(),
                vertexCacheSDKOptions.getConnectTimeout(),
                vertexCacheSDKOptions.getReadTimeout(),
                vertexCacheSDKOptions.getEncryptionMode(),
                vertexCacheSDKOptions.getPublicKey(),
                vertexCacheSDKOptions.getSharedEncryptionKey(),
                vertexCacheSDKOptions.getClientId(),
                vertexCacheSDKOptions.getClientToken()
        );
    }

    public CommandResult ping() {
        PingCommand cmd = (PingCommand) new PingCommand().execute(tcpClient);
        return new CommandResult(cmd.isSuccess(), cmd.getStatusMessage());
    }

    public CommandResult set(String key, String value) {
        SetCommand cmd = (SetCommand) new SetCommand(key, value).execute(tcpClient);
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

    public boolean isConnected() {
        return tcpClient.isConnected();
    }

    public void close() {
        tcpClient.close();
    }
}
