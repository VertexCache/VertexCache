/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.common.protocol;

import com.vertexcache.common.log.LogHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Defines the core binary message protocol used for communication between VertexCache clients and servers.
 *
 * Otherwise know as "VCMP"
 *
 * This class establishes:
 *  - Constants for message opcodes and flags
 *  - The binary layout for request and response framing
 *  - Protocol-level settings such as header sizes, limits, and reserved values
 *
 * It serves as the foundation for parsing and constructing low-level messages in VertexCache,
 * ensuring consistent wire format across all clients and server components.
 *
 * Note: This protocol is designed for efficiency and compactness, optimized for high-throughput cache access.
 */
public class VertexCacheMessageProtocol {

    private static final String SYSTEM_ERROR = "ERR, Unable to convert to bytes";

    public static final byte STRING_PREFIX = '+';
    private static final byte ERROR_PREFIX = '-';
    private static final byte INTEGER_PREFIX = '!';
    private static final byte ARRAY_PREFIX = '[';
    private static final byte ARRAY_SUFFIX = ']';
    private static final byte STRING_ARRAY_PREFIX = '#';
    private static final byte CARRIAGE_RETURN = '\r';
    private static final byte LINE_FEED = '\n';

    public static byte[] encodeString(String value) {
        return getBytes(value, STRING_PREFIX);
    }

    public static byte[] encodeError(String value) {
        return getBytes(value, ERROR_PREFIX);
    }

    public static byte[] encodeInteger(long value) {
        return getBytes(Long.toString(value), INTEGER_PREFIX);
    }

    public static byte[] encodeArray(byte[][] values) {
        ByteArrayOutputStream output = getByteArrayOutputStream(Long.toString(values.length), ARRAY_PREFIX);

        try {
            for (byte[] value : values) {
                output.write(STRING_ARRAY_PREFIX);
                output.write(value);
                output.write(CARRIAGE_RETURN);
                output.write(LINE_FEED);
            }

            output.write(getBytes("", ARRAY_SUFFIX));
            return output.toByteArray();

        } catch (IOException e) {
            LogHelper.getInstance().logFatal(e.getMessage());
            return getBytes(SYSTEM_ERROR, ERROR_PREFIX);
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                LogHelper.getInstance().logFatal(e.getMessage());
            }
        }
    }

    private static byte[] getBytes(String value, byte stringPrefix) {
        return getByteArrayOutputStream(value, stringPrefix).toByteArray();
    }

    private static ByteArrayOutputStream getByteArrayOutputStream(String value, byte stringPrefix) {
        return getByteArrayOutputStream(value.getBytes(StandardCharsets.UTF_8), stringPrefix);
    }

    private static ByteArrayOutputStream getByteArrayOutputStream(byte[] value, byte stringPrefix) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            if (stringPrefix != STRING_ARRAY_PREFIX) {
                output.write(stringPrefix);
            }
            output.write(value);
            output.write(CARRIAGE_RETURN);
            output.write(LINE_FEED);
            return output;
        } catch (IOException e) {
            LogHelper.getInstance().logFatal(e.getMessage());
            return getByteArrayOutputStream(SYSTEM_ERROR, ERROR_PREFIX);
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                LogHelper.getInstance().logFatal(e.getMessage());
            }
        }
    }
}
