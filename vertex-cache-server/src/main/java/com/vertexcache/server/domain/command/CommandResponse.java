package com.vertexcache.server.domain.command;

import com.vertexcache.common.protocol.VertexCacheMessageProtocol;

public class CommandResponse {

    private static final String RESPONSE_OK = "OK";
    private static final String RESPONSE_NIL = "(nil)";

    private boolean isOK;

    private String message;

    public void setResponse(String message) {
        this.isOK = true;
        this.message = message;
    }

    public void setResponseOK() {
        this.isOK = true;
        this.message = RESPONSE_OK;
    }

    public void setResponseError(String message) {
        this.isOK = false;
        this.message = message;
    }

    public void setResponseNil() {
        this.isOK = true;
        this.message = RESPONSE_NIL;
    }

    public boolean isOK() {
        return isOK;
    }

    public void setOK(boolean OK) {
        isOK = OK;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] toVCMPAsBytes() {
        if(this.isOK) {
            return VertexCacheMessageProtocol.encodeString(this.message);
        } else {
            return VertexCacheMessageProtocol.encodeError(this.message);
        }
    }

    public String toVCMPAsString() {
        return this.toVCMPAsBytes().toString();
    }

}
