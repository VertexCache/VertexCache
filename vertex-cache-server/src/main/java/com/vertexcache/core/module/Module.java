package com.vertexcache.core.module;

public abstract class Module implements ModuleHandler {

    @Override
    public final void start() {
        System.out.println("Starting " + this.getClass().getSimpleName());
        onStart();
    }

    @Override
    public final void stop() {
        System.out.println("Stopping " + this.getClass().getSimpleName());
        onStop();
    }

    protected abstract void onStart();
    protected abstract void onStop();
}
