package com.vertexcache.service.command;

import com.vertexcache.common.protocol.VertexCacheMessageProtocol;

public class CommandResponse {

    private boolean isOK;

    private String message;


    public CommandResponse(boolean isOK, String message) {
        this.isOK = isOK;
        this.message = message;
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
