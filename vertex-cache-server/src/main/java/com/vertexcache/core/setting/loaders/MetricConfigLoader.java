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
package com.vertexcache.core.setting.loaders;

import com.vertexcache.core.setting.ConfigKey;
import com.vertexcache.core.setting.model.LoaderBase;

/**
 * Configuration loader responsible for validating settings related to internal metric collection.
 *
 * Handles options such as:
 * - Whether metrics are enabled
 * - Granularity and retention of in-memory metric data
 * - Optional features like hot key tracking
 *
 * Ensures that the MetricModule is initialized with valid parameters and aligned with
 * system observability goals. Metrics will not be collected if disabled or misconfigured.
 *
 * This loader runs during startup to guarantee metric safety and consistency.
 */
public class MetricConfigLoader extends LoaderBase {

    private boolean enableMetric;

    @Override
    public void load() {

        this.enableMetric = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_METRIC,ConfigKey.ENABLE_METRIC_DEFAULT);
    }

    public boolean isEnableMetric() {
        return enableMetric;
    }

    public void setEnableMetric(boolean enableMetric) {
        this.enableMetric = enableMetric;
    }
}
