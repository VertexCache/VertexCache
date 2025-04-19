package com.vertexcache.core.command;

import com.vertexcache.common.protocol.VertexCacheMessageProtocol;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CommandResponse {

    private static final String RESPONSE_OK = "OK";
    private static final String RESPONSE_NIL = "(nil)";

    private boolean isOK;
    private String message;
    private byte[] rawBytes;

    public void setResponse(String message) {
        this.isOK = true;
        this.message = message;
        this.rawBytes = null;
    }

    public void setResponse(byte[] rawBytes) {
        this.isOK = true;
        this.rawBytes = rawBytes;
        this.message = null;
    }

    public void setResponseFromArray(String[] lines) {
        byte[][] encoded = new byte[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            encoded[i] = lines[i].getBytes(StandardCharsets.UTF_8);
        }
        setResponse(VertexCacheMessageProtocol.encodeArray(encoded));
    }

    public void setResponseFromArray(List<String> lines) {
        setResponseFromArray(lines.toArray(new String[0]));
    }

    public void setResponseOK() {
        this.isOK = true;
        this.message = RESPONSE_OK;
        this.rawBytes = null;
    }

    public void setResponseError(String message) {
        this.isOK = false;
        this.message = message;
        this.rawBytes = null;
    }

    public void setResponseNil() {
        this.isOK = true;
        this.message = RESPONSE_NIL;
        this.rawBytes = null;
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
        this.rawBytes = null;
    }

    public byte[] toVCMPAsBytes() {
        if (rawBytes != null) {
            return rawBytes;
        }

        if (this.isOK) {
            return VertexCacheMessageProtocol.encodeString(this.message);
        } else {
            return VertexCacheMessageProtocol.encodeError(this.message);
        }
    }

    public String toVCMPAsString() {
        return new String(toVCMPAsBytes());
    }
}
