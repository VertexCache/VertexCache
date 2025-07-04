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
package com.vertexcache.module.restapi.server;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.security.PemUtil;
import com.vertexcache.core.setting.Config;
import com.vertexcache.core.setting.loaders.RestApiConfigLoader;
import com.vertexcache.module.restapi.exception.VertexCacheRestApiException;
import com.vertexcache.module.restapi.middleware.RestApiAuthMiddleware;
import com.vertexcache.module.restapi.routes.ApiExceptionRoutes;
import com.vertexcache.module.restapi.routes.ApiRoutes;

import io.javalin.Javalin;
import io.javalin.community.ssl.SslPlugin;
import io.javalin.config.JavalinConfig;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * REST API server implementation for VertexCache.
 *
 * Manages the lifecycle of the Javalin web server, including configuration,
 * route registration, middleware setup, and optional TLS support.
 *
 * Supports starting with or without TLS based on configuration,
 * CORS configuration, authentication middleware, and graceful shutdown.
 */
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
            configCors(conf);
        });
        configMiddleware(app);
        configRoutes(app);
        app.start(config.getPort());
        LogHelper.getInstance().logInfo("[REST API] Started on HTTP port " + config.getPort());
    }

    private void startWithTls() throws Exception {
        try {
            this.app = Javalin.create(conf -> {
                conf.showJavalinBanner = false;
                try {
                    conf.registerPlugin(this.configSSL());
                } catch (VertexCacheRestApiException e) {
                    throw new RuntimeException(e);
                }
                configCors(conf);
            });

            configMiddleware(app);
            configRoutes(app);

            app.start();
        } catch (Exception e) {
            LogHelper.getInstance().logError("[REST API] TLS startup failure " + e.getMessage());
            throw e;
        }
    }

    private Path writeToTempFile(@NotNull String content, String prefix, String suffix) throws Exception {
        Path tempFile = Files.createTempFile(prefix, suffix);
        Files.writeString(tempFile, content, StandardCharsets.UTF_8);
        tempFile.toFile().deleteOnExit();
        return tempFile;
    }

    private void configRoutes(Javalin app) {
        ApiRoutes.register(app);
        ApiExceptionRoutes.register(app);
    }

    private void configCors(JavalinConfig conf) {
        if (Config.getInstance().getRestApiConfigLoader().isAllowCors()) {
            conf.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
        }
    }

    private void configMiddleware(Javalin app) {
        if (Config.getInstance().getRestApiConfigLoader().isRequireAuth()) {
            // Enforce Auth Check
            app.before(new RestApiAuthMiddleware());
        }
    }

    private SslPlugin configSSL() throws VertexCacheRestApiException {
        SslPlugin plugin = null;
        try {
            String certPem = Config.getInstance().getSecurityConfigLoader().getTlsCertificate();
            String keyPem = Config.getInstance().getSecurityConfigLoader().getTlsPrivateKey();
            int tlsPort = Config.getInstance().getRestApiConfigLoader().getPortTls();

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
        } catch (Exception e) {
            throw new VertexCacheRestApiException("Missing or invalid TLS certs.");
        }
        return plugin;
    }
}
