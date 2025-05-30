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
package com.vertexcache.core.module.model;

/**
 * Base interface for all pluggable modules in the VertexCache system.
 *
 * Defines the standard lifecycle methods that each module must implement, including:
 * - Initialization
 * - Startup and shutdown hooks
 * - Configuration validation or extension (if needed)
 *
 * Modules can encapsulate features like clustering, alerting, metrics, REST API support, etc.
 * This interface allows the core server to discover and manage modules in a consistent manner.
 */
public abstract class Module implements ModuleHandler {

    private ModuleStatus moduleStatus = ModuleStatus.NOT_STARTED;
    private String message = "";

    @Override
    public final void start() {
        try {
            onValidate();
            if (this.moduleStatus == ModuleStatus.NOT_STARTED) {
                onStart();
            }
            if (this.moduleStatus == ModuleStatus.NOT_STARTED) {
                setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL);
            }
        } catch (Exception e) {
            setModuleStatus(ModuleStatus.ERROR_LOAD, e.getMessage());
            throw e;
        }
    }

    @Override
    public final void stop() {
        try {
            onStop();
            setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL);
        } catch (Exception e) {
            setModuleStatus(ModuleStatus.ERROR_RUNTIME, e.getMessage());
            throw e;
        }
    }

    protected abstract void onValidate();
    protected abstract void onStart();
    protected abstract void onStop();


    @Override
    public String getStatusSummary() {
        return switch (moduleStatus) {
            case STARTUP_SUCCESSFUL -> "Running";
            case STARTUP_STANDBY -> "On Standby & Ready to Run";
            case STARTUP_FAILED -> "Startup failed";
            case ERROR_LOAD -> "Load error";
            case ERROR_RUNTIME -> "Runtime error";
            case DISABLED -> "Disabled";
            default -> moduleStatus.name();
        };
    }

    public void setModuleStatus(ModuleStatus status) {
        setModuleStatus(status, "");
    }

    public void setModuleStatus(ModuleStatus status, String message) {
        this.moduleStatus = status;
        this.message = message;
    }

    public void reportHealth(ModuleStatus status, String message) {
        setModuleStatus(status, message);
    }

    public void reportHealth(ModuleStatus status) {
        reportHealth(status, "");
    }

    public ModuleStatus getModuleStatus() {
        return this.moduleStatus;
    }

    public String getStatusMessage() {
        return this.message;
    }

}
