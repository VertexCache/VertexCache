package com.vertexcache.module.ratelimiter;

import com.vertexcache.core.module.Module;

public class RateLimiterModule  extends Module {

    @Override
    protected void onStart() {
        System.out.println("Rate Limiter module started");
    }

    @Override
    protected void onStop() {
        System.out.println("Rate Limiter module stopped");
    }
}
