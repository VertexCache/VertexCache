package com.vertexcache.module.alert.service;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.module.alert.model.AlertEvent;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AlertWebhookDispatcher {

    private final String webhookUrl;
    private final boolean signingEnabled;
    private final String signingSecret;
    private final int timeoutMs;
    private final int maxRetries;
    private final Gson gson = new Gson();

    public AlertWebhookDispatcher(String webhookUrl, boolean signingEnabled, String signingSecret, int timeoutMs, int maxRetries) {
        this.webhookUrl = webhookUrl;
        this.signingEnabled = signingEnabled;
        this.signingSecret = signingSecret;
        this.timeoutMs = timeoutMs;
        this.maxRetries = maxRetries;
    }

    public void dispatch(AlertEvent event) {
        String payload = gson.toJson(event);
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        int attempts = 0;

        while (attempts <= maxRetries) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(webhookUrl).openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(timeoutMs);
                conn.setReadTimeout(timeoutMs);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                if (signingEnabled && signingSecret != null && !signingSecret.isEmpty()) {
                    conn.setRequestProperty("X-VC-Signature", signingSecret); // Placeholder for real HMAC later
                }

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payloadBytes);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    LogHelper.getInstance().logInfo("Alert dispatched: " + event.getEvent());
                    return;
                } else {
                    LogHelper.getInstance().logWarn("Dispatch failed (HTTP " + responseCode + "): " + event.getEvent());
                }
            } catch (Exception ex) {
                LogHelper.getInstance().logWarn("Dispatch attempt " + attempts + " failed: " + ex.getMessage());
            }

            attempts++;
        }

        LogHelper.getInstance().logFatal("Failed to dispatch alert after " + maxRetries + " retries: " + event.getEvent());
    }
}
