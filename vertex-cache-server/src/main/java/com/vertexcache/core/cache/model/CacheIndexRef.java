package com.vertexcache.core.cache.model;

public class CacheIndexRef {
    public final Object idx1;
    public final Object idx2;

    public CacheIndexRef(Object idx1, Object idx2) {
        this.idx1 = idx1;
        this.idx2 = idx2;
    }

    public Object getIdx1() {
        return idx1;
    }

    public Object getIdx2() {
        return idx2;
    }
}
