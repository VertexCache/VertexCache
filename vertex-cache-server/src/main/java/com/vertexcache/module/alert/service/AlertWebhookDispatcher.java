package com.vertexcache.module.alert.service;

import com.google.gson.Gson;
import com.vertexcache.module.alert.model.AlertEvent;
import okhttp3.*;

import java.io.IOException;
import java.util.logging.Logger;

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
