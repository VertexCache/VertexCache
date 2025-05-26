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
package com.vertexcache.core.validation.validators;

import com.vertexcache.core.setting.Config;
import com.vertexcache.core.validation.Validator;
import com.vertexcache.module.ratelimiter.exception.VertexCacheRateLimitModuleException;

public class RateLimitValidator implements Validator {

    private final String rateStr;
    private final String burstStr;

    public RateLimitValidator(String rateStr, String burstStr) {
        this.rateStr = rateStr;
        this.burstStr = burstStr;
    }

    public RateLimitValidator() {
        this(Config.getInstance().getRateLimitingConfigLoader().getRateLimitTokensTerSecond(),
                Config.getInstance().getRateLimitingConfigLoader().getRateLimitBurst());
    }

    @Override
    public void validate() {
        int rate, burst;

        try {
            rate = Integer.parseInt(rateStr);
        } catch (NumberFormatException e) {
            throw new VertexCacheRateLimitModuleException(
                    "Invalid integer for 'rate_limit_tokens_per_second': " + rateStr);
        }

        try {
            burst = Integer.parseInt(burstStr);
        } catch (NumberFormatException e) {
            throw new VertexCacheRateLimitModuleException(
                    "Invalid integer for 'rate_limit_burst': " + burstStr);
        }

        if (rate < 1) {
            throw new VertexCacheRateLimitModuleException(
                    "'rate_limit_tokens_per_second' must be greater than 0");
        }

        if (burst < 1) {
            throw new VertexCacheRateLimitModuleException(
                    "'rate_limit_burst' must be greater than 0");
        }

        if (burst < rate) {
            throw new VertexCacheRateLimitModuleException(
                    "'rate_limit_burst' must be greater than or equal to 'rate_limit_tokens_per_second' " +
                            "(burst=" + burst + ", rate=" + rate + ")");
        }
    }
}
