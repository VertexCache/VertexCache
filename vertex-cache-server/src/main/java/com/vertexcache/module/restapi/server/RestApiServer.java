package com.vertexcache.module.restapi.server;

import com.vertexcache.core.setting.Config;
import com.vertexcache.module.restapi.middleware.RestApiAuthMiddleware;
import com.vertexcache.module.restapi.routes.ApiExceptionRoutes;
import com.vertexcache.module.restapi.routes.ApiRoutes;
import com.vertexcache.common.log.LogHelper;
import io.javalin.Javalin;

public class RestApiServer {

    private Javalin app;

    public void start(int port) {
        this.app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        });

        boolean requireAuth = Config.getInstance().getRestApiConfigLoader().isRequireAuth();
        if (requireAuth) {
            app.before(new RestApiAuthMiddleware());
            LogHelper.getInstance().logInfo("[REST API] Auth middleware enabled");
        } else {
            LogHelper.getInstance().logWarn("[REST API] Auth middleware DISABLED (open access mode)");
        }

        ApiRoutes.register(app);
        ApiExceptionRoutes.register(app);
        app.start(port);
    }

    public void stop() {
        if (app != null) {
            app.stop();
        }
    }
}
