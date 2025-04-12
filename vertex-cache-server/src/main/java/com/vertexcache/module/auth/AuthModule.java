package com.vertexcache.module.auth;

import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.module.auth.AuthService;
import com.vertexcache.module.auth.AuthInitializer;
import com.vertexcache.core.setting.Config;

public class AuthModule extends Module {

    private AuthService authService;

    @Override
    protected void onStart() {
        Config config = Config.getInstance();
        //this.authService = AuthInitializer.initialize(
                //config.getAuthHydrateFile(),
               // config.getAuthDbFile()
        //);
        this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
    }

    @Override
    protected void onStop() {
        // cleanup logic
        this.setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }

    @Override
    protected void onError() {
        // cleanup
        this.setModuleStatus(ModuleStatus.ERROR_RUNTIME,"Generic Error, to be updated, the real reason");
    }

    public AuthService getAuthService() {
        return authService;
    }
}
