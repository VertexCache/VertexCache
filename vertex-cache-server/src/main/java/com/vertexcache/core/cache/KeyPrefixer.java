package com.vertexcache.core.cache;

import com.vertexcache.core.setting.Config;
import com.vertexcache.server.session.ClientSessionContext;

public class KeyPrefixer {

    private static final String SEPARATOR = "::";

    public static String prefixKey(String key, ClientSessionContext context) {
        if (!Config.getInstance().getConfigAuthWithTenant().isAuthEnabled() ||
                !Config.getInstance().getConfigAuthWithTenant().isTenantKeyPrefixingEnabled() ||
                context == null ||
                context.getTenantId() == null) {
            return key;
        }
        return context.getTenantId().getValue() + SEPARATOR + key;
    }

    public static String removePrefix(String fullKey, ClientSessionContext context) {
        if (!Config.getInstance().getConfigAuthWithTenant().isAuthEnabled() ||
                !Config.getInstance().getConfigAuthWithTenant().isTenantKeyPrefixingEnabled() ||
                context == null ||
                context.getTenantId() == null) {
            return fullKey;
        }

        String prefix = context.getTenantId().getValue() + SEPARATOR;
        return fullKey.startsWith(prefix) ? fullKey.substring(prefix.length()) : fullKey;
    }
}
