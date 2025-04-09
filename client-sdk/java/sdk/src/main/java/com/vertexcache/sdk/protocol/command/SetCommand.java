package com.vertexcache.sdk.protocol.command;

import com.vertexcache.sdk.protocol.BaseCommand;
import com.vertexcache.sdk.protocol.CommandType;
import com.vertexcache.sdk.result.VertexCacheSdkException;

public class SetCommand extends BaseCommand<SetCommand> {

    private final String primaryKey;
    private final String value;
    private final String secondaryKey;
    private final String tertiaryKey;

    public SetCommand(String primaryKey, String value) throws VertexCacheSdkException {
        this(primaryKey, value, null, null);
    }

    public SetCommand(String primaryKey, String value, String secondaryKey) throws VertexCacheSdkException {
        this(primaryKey, value, secondaryKey, null);
    }

    public SetCommand(String primaryKey, String value, String secondaryKey, String tertiaryKey) throws VertexCacheSdkException {

        if(primaryKey == null || primaryKey.isBlank()) {
            throw new VertexCacheSdkException("Missing Primary Key");
        }

        if(value == null || value.isBlank()) {
            throw new VertexCacheSdkException("Missing Value");
        }

        this.primaryKey = primaryKey;
        this.value = value;
        this.secondaryKey = secondaryKey;
        this.tertiaryKey = tertiaryKey;
    }

    @Override
    protected String buildCommand() {
        StringBuilder sb = new StringBuilder();
        sb.append(CommandType.SET).append(BaseCommand.COMMAND_SPACER)
                .append(primaryKey).append(BaseCommand.COMMAND_SPACER)
                .append(value);

        if (secondaryKey != null && !secondaryKey.isBlank()) {
            sb.append(" ").append(CommandType.IDX1).append(" ").append(secondaryKey);
        }

        if (tertiaryKey != null && !tertiaryKey.isBlank()) {
            sb.append(" ").append(CommandType.IDX2).append(" ").append(tertiaryKey);
        }

        return sb.toString();
    }

    protected void parseResponse(String responseBody) {
        if(!responseBody.equalsIgnoreCase("OK")) {
            this.setFailure("OK Not received");
        } else {
            this.setSuccess();
        }
    }
}
