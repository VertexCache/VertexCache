package com.vertexcache.core.setting.loader;

import com.vertexcache.common.config.reader.ConfigLoader;
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
