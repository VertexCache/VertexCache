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
package com.vertexcache.module.alert.service;

import com.google.gson.Gson;
import com.vertexcache.module.alert.model.AlertEvent;
import okhttp3.*;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * AlertWebhookDispatcher is responsible for delivering alert events to the configured
 * external webhook endpoint. It serializes AlertEvent data and performs the HTTP POST
 * operation, handling any network or response errors according to retry policy.
 *
 * This class is a key component of the alerting infrastructure in VertexCache,
 * enabling integration with external monitoring, notification, or incident response systems.
 */
public class AlertWebhookDispatcher {
    private static final Logger LOG = Logger.getLogger(AlertWebhookDispatcher.class.getSimpleName());
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private final String webhookUrl;
    private final boolean signingEnabled;
    private final String signingSecret;
    private final int maxRetries;

    public AlertWebhookDispatcher(String webhookUrl,
                                  boolean signingEnabled,
                                  String signingSecret,
                                  int timeoutMs,
                                  int maxRetries) {
        this.webhookUrl = webhookUrl;
        this.signingEnabled = signingEnabled;
        this.signingSecret = signingSecret;
        this.maxRetries = maxRetries;
    }

    public void dispatch(AlertEvent event) {
        String payload = gson.toJson(event);
        RequestBody body = RequestBody.create(payload, JSON);

        Request.Builder builder = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .header("Content-Type", "application/json");

        if (signingEnabled && signingSecret != null && !signingSecret.isBlank()) {
            builder.header("X-VC-Signature", signingSecret);
        }

        Request request = builder.build();

        int attempt = 0;
        while (attempt <= maxRetries) {
            try (Response response = client.newCall(request).execute()) {
                int code = response.code();
                if (code >= 200 && code < 300) {
                    LOG.fine("[AlertWebhookDispatcher] Alert dispatched successfully (" + code + "): " + event.getEvent());
                    return;
                } else {
                    LOG.warning("[AlertWebhookDispatcher] Attempt " + attempt + " failed with HTTP " + code);
                }
            } catch (IOException e) {
                LOG.warning("[AlertWebhookDispatcher] Attempt " + attempt + " failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
            attempt++;
        }

        LOG.severe("[AlertWebhookDispatcher] Failed to dispatch alert after " + maxRetries + " retries: " + event.getEvent());
    }
}
