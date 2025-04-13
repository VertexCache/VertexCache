package com.vertexcache.module.auth;

import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;

public class AuthModule extends Module {

    private AuthService authService;

    @Override
    protected void onStart() {
        try {
            if (Config.getInstance().getRawAuthClientEntries().isEmpty()) {
                throw new VertexCacheAuthInitializationException("Require at least one client defined when auth is enabled.");
            }

            this.authService = AuthInitializer.initializeFromEnv();

            reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Auth clients loaded");

        } catch (VertexCacheAuthInitializationException e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        this.authService = null;
        setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

    public AuthService getAuthService() {
        return authService;
    }
}
