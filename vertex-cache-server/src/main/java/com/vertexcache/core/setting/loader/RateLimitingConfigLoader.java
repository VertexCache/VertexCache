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
package com.vertexcache.core.setting.loader;

import com.vertexcache.core.setting.ConfigKey;

public class RateLimitingConfigLoader extends LoaderBase {

    private boolean enableRateLimit;
    private String rateLimitTokensTerSecond;
    private String rateLimitBurst;

    @Override
    public void load() {
        this.enableRateLimit = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_RATE_LIMIT,ConfigKey.ENABLE_RATE_LIMIT_DEFAULT);

        // Lazy Load the remaining settings
        if(this.enableRateLimit) {
            if (this.getConfigLoader().isExist(ConfigKey.RATE_LIMIT_TOKENS_PER_SECOND)) {
                this.rateLimitTokensTerSecond = this.getConfigLoader().getProperty(ConfigKey.RATE_LIMIT_TOKENS_PER_SECOND);
            }
            if (this.getConfigLoader().isExist(ConfigKey.RATE_LIMIT_BURST)) {
                this.rateLimitBurst = this.getConfigLoader().getProperty(ConfigKey.RATE_LIMIT_BURST);
            }
        }
    }

    public boolean isRateLimitEnabled() { return enableRateLimit; }
    public String getRateLimitTokensTerSecond() { return rateLimitTokensTerSecond; }
    public String getRateLimitBurst() { return rateLimitBurst; }
    public void setEnableRateLimit(boolean enableRateLimit) {this.enableRateLimit = enableRateLimit;}
    public void setRateLimitTokensTerSecond(String rateLimitTokensTerSecond) {this.rateLimitTokensTerSecond = rateLimitTokensTerSecond;}
    public void setRateLimitBurst(String rateLimitBurst) {this.rateLimitBurst = rateLimitBurst;}
}
