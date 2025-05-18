package com.vertexcache.module.restapi.server;


import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.security.PemUtil;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.setting.loader.RestApiConfigLoader;
import com.vertexcache.module.restapi.middleware.RestApiAuthMiddleware;
import com.vertexcache.module.restapi.routes.ApiExceptionRoutes;
import com.vertexcache.module.restapi.routes.ApiRoutes;

import io.javalin.Javalin;
import io.javalin.community.ssl.SslPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public class RestApiServer {

    public final static String API_PREFIX = "/api";
    private final RestApiConfigLoader config;
    private Javalin app;

    public RestApiServer() {
        this.config = Config.getInstance().getRestApiConfigLoader();
    }

    public void start() throws Exception {
        if (config.isRequireTls()) {
           startWithTls();
        } else {
            startWithoutTls();
        }
    }

    public void stop() {
        if (app != null) {
            app.stop();
        }
    }

    private void startWithoutTls() {
        this.app = Javalin.create(conf -> {
            conf.showJavalinBanner = false;
        });

        // Auth middleware
        if (config.isRequireAuth()) {
            app.before(new RestApiAuthMiddleware());
            LogHelper.getInstance().logInfo("[REST API] Auth middleware enabled");
        } else {
            LogHelper.getInstance().logWarn("[REST API] Auth middleware DISABLED (open access mode)");
        }

        // Register routes
        ApiRoutes.register(app);
        ApiExceptionRoutes.register(app);

        // TLS or plain HTTP
        app.start(config.getPort());
        LogHelper.getInstance().logInfo("[REST API] Started on HTTP port " + config.getPort());
    }

    private void startWithTls() throws Exception {
        String certPem = Config.getInstance().getSecurityConfigLoader().getTlsCertificate();
        String keyPem = Config.getInstance().getSecurityConfigLoader().getTlsPrivateKey();
        int tlsPort = Config.getInstance().getRestApiConfigLoader().getPortTls();

        SslPlugin plugin;

        if (PemUtil.isFilePath(certPem) && PemUtil.isFilePath(keyPem)) {
            plugin = new SslPlugin(conf -> {
                conf.pemFromPath(certPem, keyPem);
                conf.securePort = tlsPort;
                conf.insecure = false;
            });
        } else {
            byte[] certBytes = PemUtil.loadPemContent(certPem).getBytes(StandardCharsets.UTF_8);
            byte[] keyBytes = PemUtil.loadPemContent(keyPem).getBytes(StandardCharsets.UTF_8);

            plugin = new SslPlugin(conf -> {
                conf.pemFromInputStream(
                        new ByteArrayInputStream(certBytes),
                        new ByteArrayInputStream(keyBytes)
                );
                conf.securePort = tlsPort;
                conf.insecure = false;
            });
        }

        this.app = Javalin.create(javalinConfig -> {
            javalinConfig.showJavalinBanner = false;
            javalinConfig.registerPlugin(plugin);
        });

        // Auth middleware
        if (Config.getInstance().getRestApiConfigLoader().isRequireAuth()) {
            app.before(new RestApiAuthMiddleware());
            LogHelper.getInstance().logInfo("[REST API] Auth middleware enabled");
        } else {
            LogHelper.getInstance().logWarn("[REST API] Auth middleware DISABLED (open access mode)");
        }

        ApiRoutes.register(app);
        ApiExceptionRoutes.register(app);

        try {
            app.start();
        } catch (Exception e) {
            LogHelper.getInstance().logError("[REST API] TLS startup failure " + e.getMessage());
            throw e;
        }

        LogHelper.getInstance().logInfo("[REST API] Started with TLS on port " + tlsPort);
    }

    private Path writeToTempFile(@NotNull String content, String prefix, String suffix) throws Exception {
        Path tempFile = Files.createTempFile(prefix, suffix);
        Files.writeString(tempFile, content, StandardCharsets.UTF_8);
        tempFile.toFile().deleteOnExit();
        return tempFile;
    }
}
