package com.vertexcache.core.module;

public enum ModuleName {
    AUTH("AuthModule"),
    RATELIMITER("RateLimiterModule"),
    METRIC("MetricModule"),
    REST_API("RestApiModule"),
    CLUSTER("ClusterModule"),
    ADMIN("AdminModule"),
    ALERT("AlertModule"),
    INTELLIGENCE("IntelligenceModule"),
    EXPORTER("ExporterModule");

    private final String value;

    ModuleName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
