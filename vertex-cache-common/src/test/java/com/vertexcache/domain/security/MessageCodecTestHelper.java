package com.vertexcache.domain.security;

import com.vertexcache.common.security.MessageCodec;

// In test folder only
public class MessageCodecTestHelper {
    public static void setProtocolVersionForTest(int version) {
        try {
            var field = MessageCodec.class.getDeclaredField("protocolVersion");
            field.setAccessible(true);
            field.setInt(null, version);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
