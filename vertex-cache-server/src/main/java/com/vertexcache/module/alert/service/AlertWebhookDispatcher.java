package com.vertexcache.module.alert.service;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.util.retry.RetryPolicy;
import com.vertexcache.core.validation.VertexCacheValidationException;
import com.vertexcache.common.log.LogHelper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AlertWebhookDispatcher {

    private final String webhookUrl;
    private final String signingSecret;
    private final int timeoutMs;
    private final int maxRetries;
    private final RetryPolicy retryPolicy;

    public AlertWebhookDispatcher(RetryPolicy retryPolicy) {
        var cfg = Config.getInstance().getAlertConfigLoader();
        this.webhookUrl = cfg.getAlertWebhookUrl();
        this.signingSecret = cfg.getAlertWebhookSigningUrl();
        this.timeoutMs = cfg.getAlertWebhookTimeout();
        this.maxRetries = cfg.getAlertWebhookRetryCount();
        this.retryPolicy = retryPolicy;
    }

    public void dispatch(String payloadJson) {
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                send(payloadJson);
                LogHelper.getInstance().logInfo("[AlertWebhook] dispatched successfully");
                return;
            } catch (Exception e) {
                LogHelper.getInstance().logWarn(String.format(
                        "[AlertWebhook] attempt=%d failed: %s", attempt, e.getMessage()
                ));

                if (attempt < maxRetries) {
                    int delay = retryPolicy.computeDelayForAttempt(attempt);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }

        LogHelper.getInstance().logError("[AlertWebhook] all retries failed");
    }

    private void send(String payloadJson) throws Exception {
        URL url = new URL(webhookUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(timeoutMs);
        conn.setReadTimeout(timeoutMs);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        if (signingSecret != null && !signingSecret.isBlank()) {
            String signature = computeSignature(payloadJson);
            conn.setRequestProperty("X-Signature", signature);
        }

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payloadJson.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) return;

        throw new VertexCacheValidationException("Webhook response code: " + responseCode);
    }

    private String computeSignature(String payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(signingSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(key);
        byte[] hmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return "sha256=" + bytesToHex(hmac);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
