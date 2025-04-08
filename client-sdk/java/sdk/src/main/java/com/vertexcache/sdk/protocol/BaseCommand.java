package com.vertexcache.sdk.protocol;

import com.vertexcache.sdk.result.VertexCacheSdkException;
import com.vertexcache.sdk.transport.TcpClient;

public abstract class BaseCommand implements Command {

    protected static final String COMMAND_SPACER = " ";

    private boolean success;
    private String response;
    private String error;

    @Override
    public Command execute(TcpClient client) {
        try {
            String raw = client.send(buildCommand()).trim();

            if (raw.startsWith("+")) {
                response = raw.substring(1);
                parseResponse(response);
                if (error == null) {
                    success = true;
                }
            } else if (raw.startsWith("-")) {
                success = false;
                error = raw.substring(1); // remove '-'
            } else {
                success = false;
                error = "Unexpected response: " + raw;
            }

        } catch (VertexCacheSdkException e) {
            success = false;
            error = e.getMessage();
        }
        return this;
    }

    protected abstract String buildCommand();

    protected void parseResponse(String responseBody) {
        // Default: do nothing — override if needed
    }

    public void setFailure(String response) {
        this.success = false;
        this.error = response;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getResponse() {
        return response;
    }

    @Override
    public String getError() {
        return error;
    }
}
