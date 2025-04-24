package com.vertexcache.module.ratelimiter;

import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.validation.validators.RateLimitValidator;
import com.vertexcache.module.ratelimiter.impl.TokenBucketRateLimiter;
import com.vertexcache.core.setting.Config;

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
