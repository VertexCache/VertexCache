package com.vertexcache.sdk.protocol.command;

import com.vertexcache.sdk.protocol.BaseCommand;
import com.vertexcache.sdk.protocol.CommandType;

public class SetCommand extends BaseCommand {

    private final String primaryKey;
    private final String value;
    private final String secondaryKey;
    private final String tertiaryKey;

    public SetCommand(String primaryKey, String value) {
        this(primaryKey, value, null, null);
    }

    public SetCommand(String primaryKey, String value, String secondaryKey) {
        this(primaryKey, value, secondaryKey, null);
    }

    public SetCommand(String primaryKey, String value, String secondaryKey, String tertiaryKey) {
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
        }
    }
}
