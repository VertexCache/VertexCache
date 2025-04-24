package com.vertexcache.module.auth;

import com.vertexcache.core.module.ModuleName;
import com.vertexcache.core.module.ModuleRegistry;

import java.util.Optional;

public class AuthModuleHelper {

    private AuthModuleHelper() {
        // Prevent instantiation
    }

    /**
     * Safely retrieves the AuthModule from the ModuleRegistry, if loaded.
     */
    public static Optional<AuthModule> getInstance() {
        return ModuleRegistry.getInstance()
                .getModuleByEnum(ModuleName.AUTH)
                .filter(m -> m instanceof AuthModule)
                .map(m -> (AuthModule) m);
    }

    /**
     * Safely retrieves the AuthService from the AuthModule, if available.
     */
    public static Optional<AuthService> getAuthService() {
        return getInstance().map(AuthModule::getAuthService);
    }
}
