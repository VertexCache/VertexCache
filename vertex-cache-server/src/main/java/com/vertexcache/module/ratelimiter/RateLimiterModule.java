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
package com.vertexcache.module.ratelimiter;

import com.vertexcache.core.module.model.Module;
import com.vertexcache.core.module.model.ModuleStatus;
import com.vertexcache.core.validation.validators.RateLimitValidator;
import com.vertexcache.module.ratelimiter.model.TokenBucketRateLimiter;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.ratelimiter.service.RateLimiterManager;

public class RateLimiterModule extends Module {

    @Override
    protected void onValidate() {
        try {
            new RateLimitValidator().validate();
        } catch (Exception e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, "Validation failed: " + e.getMessage());
            throw e;
        }
    }

    @Override
    protected void onStart() {
        try {
            int rate = Integer.parseInt(Config.getInstance().getRateLimitingConfigLoader().getRateLimitTokensTerSecond());
            int burst = Integer.parseInt(Config.getInstance().getRateLimitingConfigLoader().getRateLimitBurst());

            TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(burst, rate);
            RateLimiterManager.getInstance().init(limiter);

            reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Rate limiter initialized with " + rate + "/sec and burst " + burst);
        } catch (Exception e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, "Unexpected error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    protected void onStop() {
        reportHealth(ModuleStatus.SHUTDOWN_SUCCESSFUL, "Rate limiter shut down.");
    }
}
