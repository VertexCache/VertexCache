package com.vertexcache.service;

import com.vertexcache.common.protocol.VertexCacheMessageProtocol;

public class CommandProcessor {

    private String inputCommand;

    public byte[] execute(byte[] requestAsBytes) {




        String[] request = new String(requestAsBytes).toLowerCase().split("\\s+");

        System.out.println("index 0: " + request[0]);

        if(request[0].equals("ping")) {
            return processSingleCommand();
        }

        /*
        if (requestAsBytes.length >= 2) {
            // Convert the first byte to an integer
            int firstByteValue = requestAsBytes[0] & 0xFF; // Ensure unsigned byte value

            // Checking the first byte of the byte array
            switch (firstByteValue) {
                case VertexCacheMessageProtocol.STRING_PREFIX:
                    return processSingleCommand();
                case 20:
                    // Do something for byte value 20
                    break;
                case 30:
                    // Do something for byte value 30
                    break;
                default:
                    // Default case
                    break;
            }
        } else {
            // Handle the case where the byte array doesn't have at least two elements
            System.out.println("Byte array is too small.");
        }

         */
        return VertexCacheMessageProtocol.encodeError("Unknown Command");
    }

    private byte[] processSingleCommand() {
        return VertexCacheMessageProtocol.encodeString("PONG");
    }
}
