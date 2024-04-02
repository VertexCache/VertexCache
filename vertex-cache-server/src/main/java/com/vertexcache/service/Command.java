package com.vertexcache.service;

public interface Command<T> {
    T execute(T... args);
}
