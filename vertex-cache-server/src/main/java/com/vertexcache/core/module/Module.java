package com.vertexcache.core.module;

public abstract class Module implements ModuleHandler {

    private ModuleStatus moduleStatus = ModuleStatus.NOT_STARTED;
    private String message = "";

    @Override
    public final void start() {
        try {
            onValidate();
            onStart();
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
            case STARTUP_FAILED -> "Startup failed";
            case ERROR_LOAD -> "Load error";
            case ERROR_RUNTIME -> "Runtime error";
            case DISABLED -> "Disabled";
            default -> moduleStatus.name();
        };
    }

    protected void setModuleStatus(ModuleStatus status) {
        setModuleStatus(status, "");
    }

    protected void setModuleStatus(ModuleStatus status, String message) {
        this.moduleStatus = status;
        this.message = message;
    }

    protected void reportHealth(ModuleStatus status, String message) { setModuleStatus(status, message);}

    protected void reportHealth(ModuleStatus status) {
        reportHealth(status, "");
    }

    public ModuleStatus getModuleStatus() {
        return this.moduleStatus;
    }

    public String getStatusMessage() {
        return this.message;
    }

}
