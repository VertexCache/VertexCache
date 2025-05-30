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
package com.vertexcache.module.exporter;

import com.vertexcache.core.module.model.Module;
import com.vertexcache.core.module.model.ModuleStatus;

/**
 * ** TO BE IMPLEMENTED (yet to be decided for future releases)
 *
 * ExporterModule is responsible for exposing internal VertexCache metrics and status data
 * to external systems for monitoring and observability. It integrates with metric registries
 * and may support various export formats or endpoints, such as Prometheus or custom sinks.
 *
 * This module enables operators to gain insight into system behavior, performance trends,
 * and alerting signals by periodically or on-demand exporting telemetry data.
 */
public class ExporterModule extends Module {

    @Override
    protected void onValidate() {
    }

    @Override
    protected void onStart() {
        this.setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
    }

    @Override
    protected void onStop() {
        this.setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
    }
}
