package com.vertexcache.core.setting;

import com.vertexcache.common.config.reader.ConfigLoader;

public class ConfigRateLimiting {

    private ConfigLoader configLoader;
    private boolean enableRateLimit;
    private String rateLimitTokensTerSecond;
    private String rateLimitBurst;


    public void load() {
        this.enableRateLimit = false;
        if (configLoader.isExist(ConfigKey.ENABLE_RATE_LIMIT)) {
            this.enableRateLimit = Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_RATE_LIMIT));
            if (configLoader.isExist(ConfigKey.RATE_LIMIT_TOKENS_PER_SECOND)) {
                this.rateLimitTokensTerSecond = configLoader.getProperty(ConfigKey.RATE_LIMIT_TOKENS_PER_SECOND);
            }
            if (configLoader.isExist(ConfigKey.RATE_LIMIT_BURST)) {
                this.rateLimitBurst = configLoader.getProperty(ConfigKey.RATE_LIMIT_BURST);
            }
        }
    }

    public void setConfigLoader(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }
    public boolean isRateLimitEnabled() { return enableRateLimit; }
    public String getRateLimitTokensTerSecond() { return rateLimitTokensTerSecond; }
    public String getRateLimitBurst() { return rateLimitBurst; }
    public void setEnableRateLimit(boolean enableRateLimit) {this.enableRateLimit = enableRateLimit;}
    public void setRateLimitTokensTerSecond(String rateLimitTokensTerSecond) {this.rateLimitTokensTerSecond = rateLimitTokensTerSecond;}
    public void setRateLimitBurst(String rateLimitBurst) {this.rateLimitBurst = rateLimitBurst;}
}
