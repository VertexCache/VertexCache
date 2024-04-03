package com.vertexcache.service.command;

public interface Command<T> {
    CommandResponse execute(T... args);
}
