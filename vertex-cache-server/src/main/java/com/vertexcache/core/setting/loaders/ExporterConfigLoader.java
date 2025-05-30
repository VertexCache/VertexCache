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
 *  ** CURRENTLY NOT USED **
 *
 * Configuration loader responsible for validating settings related to metric export.
 *
 * Handles options such as:
 * - Whether metric export is enabled
 * - Export interval and destination configuration (e.g., file path, future webhook support)
 * - Filtering or throttling behavior for exported metrics (if applicable)
 *
 * Ensures the MetricModule can safely and consistently export runtime metrics for
 * external monitoring, auditing, or analytics use cases.
 *
 * If export is disabled or misconfigured, no metrics will be written externally.
 */
public class ExporterConfigLoader extends LoaderBase {

    private boolean enableExporter;

    @Override
    public void load() {
        this.enableExporter = this.getConfigLoader().getBooleanProperty(ConfigKey.ENABLE_EXPORTER,ConfigKey.ENABLE_EXPORTER_DEFAULT);
    }

    public boolean isEnableExporter() {
        return enableExporter;
    }

    public void setEnableExporter(boolean enableExporter) {
        this.enableExporter = enableExporter;
    }
}
