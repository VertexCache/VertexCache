package com.vertexcache.module.ratelimiter;

import com.vertexcache.core.module.Module;
import com.vertexcache.core.module.ModuleStatus;
import com.vertexcache.core.setting.Config;
import com.vertexcache.module.ratelimiter.impl.TokenBucketRateLimiter;

public class RateLimiterModule extends Module {

    private static final int MIN_RATE_LIMIT = 0;
    private static final int MIN_BURST_LIMIT = 0;

    @Override
    protected void onInitialize() {

    }

    @Override
    protected void onValidate() {

    }

    @Override
    protected void onStart() {
        try {
            String rateStr = Config.getInstance().getRateLimitTokensTerSecond();
            String burstStr = Config.getInstance().getRateLimitBurst();

            int rate = Integer.parseInt(rateStr);
            int burst = Integer.parseInt(burstStr);

            if (rate <= MIN_RATE_LIMIT || burst <= MIN_BURST_LIMIT) {
                throw new VertexCacheRateLimitModuleException("Rating limiting values must be positive for 'rate_limit_tokens_per_second' and 'rate_limit_burst'.");
            }

            TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(burst, rate);
            RateLimiterManager.getInstance().init(limiter);

            reportHealth(ModuleStatus.STARTUP_SUCCESSFUL, "Rate limiter initialized with " + rate + "/sec and burst " + burst);

        } catch (VertexCacheRateLimitModuleException e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, e.getMessage());
        } catch (Exception e) {
            reportHealth(ModuleStatus.STARTUP_FAILED, "Unexpected error: " + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        reportHealth(ModuleStatus.SHUTDOWN_SUCCESSFUL, "Rate limiter shut down.");
    }
}
