package com.vertexcache.common.util;

public class StringUtil {

    public static final String esacpeQuote(String value) {
        return value.replaceAll("\\\\", "").replaceAll("^\"|\"$", "");
    }
}
