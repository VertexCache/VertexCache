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
package com.vertexcache.module.smart.service;

import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.alert.model.AlertEvent;
import com.vertexcache.module.alert.model.AlertEventType;
import com.vertexcache.module.auth.listener.AuthFailureListener;
import com.vertexcache.module.auth.model.AuthFailureContext;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Monitors and emits alerts for unauthorized access attempts, such as invalid credentials or disallowed roles.
 * Alert noise is controlled by per-IP throttling and reason-based suppression.
 */
public class UnauthorizedAccessAlertService extends BaseAlertService implements AuthFailureListener {

    private final Map<String, AtomicLong> throttleMap = new ConcurrentHashMap<>();
    private static final long ALERT_COOLDOWN_MILLIS = 10_000; // 10 seconds

    public UnauthorizedAccessAlertService() throws VertexCacheException {
        super("UnauthorizedAccess", 0); // No scheduler needed
    }

    @Override
    protected void evaluate() {
        // No-op: manual trigger only via listener
    }

    @Override
    public void onAuthFailure(AuthFailureContext context) {
        if (!Config.getInstance().getSmartConfigLoader().isEnableSmartUnauthorizedAccessAlert())
            return;

        if (this.shouldTrigger(context.getClientId())) {
            this.triggerAlert(context.getClientId());
        }
    }

    @Override
    public void onInvalidToken(String token) {
        if (!Config.getInstance().getSmartConfigLoader().isEnableSmartUnauthorizedAccessAlert())
            return;

        if (this.shouldTrigger(token)) {
            this.triggerAlert(token);
        }
    }

    private void triggerAlert(String identifier) {
        Map<String, Object> details = new HashMap<>();
        details.put("message", "Unauthorized access attempt detected for identifier: " + identifier);
        details.put("identifier", identifier);
        details.put("timestamp", Instant.now().toString());

        this.getAlertModule().dispatch(new AlertEvent(
                AlertEventType.UNAUTHORIZED_ACCESS_ATTEMPT,
                Config.getInstance().getCoreConfigLoader().getLocalNodeId(),
                details
        ));
    }

    private boolean shouldTrigger(String identifier) {
        long now = System.currentTimeMillis();
        AtomicLong last = throttleMap.computeIfAbsent(identifier, k -> new AtomicLong(0));
        long previous = last.get();

        if (now - previous >= ALERT_COOLDOWN_MILLIS) {
            last.set(now);
            return true;
        }
        return false;
    }
}
