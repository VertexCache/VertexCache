package com.vertexcache.client.protocol;

import com.vertexcache.client.exception.VertexCacheInternalClientException;
import com.vertexcache.client.transport.TcpClientInterface;

public abstract class BaseCommand<T extends BaseCommand<T>> implements Command {

    private static String RESPONSE_OK = "OK";
    protected static final String COMMAND_SPACER = " ";

    private boolean success;
    private String response;
    private String error;

    @Override
    public Command execute(TcpClientInterface client) {
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

        } catch (VertexCacheInternalClientException e) {
            success = false;
            error = e.getMessage();
        }
        return this;
    }

    protected abstract String buildCommand();
    protected abstract String getCommandKey();

    protected void parseResponse(String responseBody) {
        // Default: do nothing — override if needed
    }

    public void setFailure(String response) {
        this.success = false;
        this.error = response;
    }

    public void setSuccess() {
        this.success = true;
        this.response = BaseCommand.RESPONSE_OK;
        this.error = null;
    }

    public void setSuccess(String response) {
        this.success = true;
        this.response = response;
        this.error = null;
    }

    @Override
    public String getStatusMessage() {
        return isSuccess() ? getResponse() : getError();
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
