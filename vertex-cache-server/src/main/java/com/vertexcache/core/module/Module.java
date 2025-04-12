package com.vertexcache.core.module;

import com.vertexcache.common.log.LogHelper;

public abstract class Module implements ModuleHandler {

    private ModuleStatus moduleStatus = ModuleStatus.NOT_STARTED;
    private String message = "";

    @Override
    public final void start() {
        try {
            onStart();
            setModuleStatus(ModuleStatus.STARTUP_SUCCESSFUL, null);
        } catch (Exception e) {
            setModuleStatus(ModuleStatus.ERROR_LOAD, e.getMessage());
            throw e;
        }
    }

    @Override
    public final void stop() {
        try {
            onStop();
            setModuleStatus(ModuleStatus.SHUTDOWN_SUCCESSFUL, null);
        } catch (Exception e) {
            setModuleStatus(ModuleStatus.ERROR_RUNTIME, e.getMessage());
            throw e;
        }
    }

    protected abstract void onStart();
    protected abstract void onStop();
    protected abstract void onError();

    public String getStatusSummary() {
        return moduleStatus +
                (message != null && !message.isBlank() ? " - " + message : "");
    }

    protected void setModuleStatus(ModuleStatus status) {
        setModuleStatus(status, "");
    }

    protected void setModuleStatus(ModuleStatus status, String message) {
        this.moduleStatus = status;
        this.message = message;
    }

    protected void reportHealth(ModuleStatus status, String message) {
        setModuleStatus(status, message);
        LogHelper.getInstance().logInfo("[MODULE] " + getClass().getSimpleName() +
                " reported " + status + (message != null && !message.isBlank() ? " - " + message : ""));
    }

    protected void reportHealth(ModuleStatus status) {
        reportHealth(status, "");
    }

}
