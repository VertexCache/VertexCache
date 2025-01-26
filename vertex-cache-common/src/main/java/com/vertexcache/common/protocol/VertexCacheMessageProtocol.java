package com.vertexcache.common.protocol;

import com.vertexcache.common.log.LogHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class VertexCacheMessageProtocol {

    private static final String SYSTEM_ERROR = "ERR, Unable to convert to bytes";

    public static final byte STRING_PREFIX = '+';
    private static final byte ERROR_PREFIX = '-';
    private static final byte INTEGER_PREFIX = '!';
    private static final byte ARRAY_PREFIX = '[';
    private static final byte ARRAY_SUFFIX = ']';
    private static final byte STRING_ARRAY_PREFIX = '#';
    private static final byte CARRIAGE_RETURN = '\r';
    private static final byte LINE_FFED = '\n';

    public static byte[] encodeString(String value)  {
        return getBytes(value, STRING_PREFIX);
    }

    public static byte[] encodeError(String value) {
        return getBytes(value, ERROR_PREFIX);
    }

    public static byte[] encodeInteger(long value) {
        return getBytes(Long.toString(value), INTEGER_PREFIX);
    }

    public static byte[] encodeArray(byte[][] values) {
        // Array Prefix with Array Size, then carriage return
        ByteArrayOutputStream output = getByteArrayOutputStream(Long.toString(values.length), ARRAY_PREFIX);

        try {
            // Output each index value followed by carriage return
            for (byte[] value : values) {
                output.write(getByteArrayOutputStream(value,STRING_ARRAY_PREFIX).toByteArray());
            }
            output.write(getBytes("", ARRAY_SUFFIX));
            return output.toByteArray();
        } catch (IOException e) {
            LogHelper.getInstance().logFatal(e.getMessage());
            return getBytes(SYSTEM_ERROR,ERROR_PREFIX);
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                LogHelper.getInstance().logFatal(e.getMessage());
            }
        }
    }

    private static byte[] getBytes(String value, byte stringPrefix) {
        return getByteArrayOutputStream(value,stringPrefix).toByteArray();
    }

    private static ByteArrayOutputStream getByteArrayOutputStream(String value, byte stringPrefix) {
        return getByteArrayOutputStream(value.getBytes(StandardCharsets.UTF_8), stringPrefix);
    }

    private static ByteArrayOutputStream getByteArrayOutputStream(byte[] value, byte stringPrefix) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            if(stringPrefix != STRING_ARRAY_PREFIX) {
                output.write(stringPrefix);
            }
            output.write(value);
            output.write(CARRIAGE_RETURN);
            output.write(LINE_FFED);
            return output;
        } catch (IOException e) {
            LogHelper.getInstance().logFatal(e.getMessage());
            return getByteArrayOutputStream(SYSTEM_ERROR,ERROR_PREFIX);
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                LogHelper.getInstance().logFatal(e.getMessage());
            }
        }
    }
}
