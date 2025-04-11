package com.vertexcache.module.auth;

import com.vertexcache.core.module.Module;
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
    }

    @Override
    protected void onStop() {
        // cleanup logic
    }

    public AuthService getAuthService() {
        return authService;
    }
}
