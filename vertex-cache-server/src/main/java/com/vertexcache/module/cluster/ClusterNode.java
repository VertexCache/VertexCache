package com.vertexcache.module.cluster;

/**
 * @param status optional: active, standby, offline
 */
public record ClusterNode(String id, String role, String host, int port, String status) {

    @Override
    public String toString() {
        return String.format("ClusterNode{id='%s', role='%s', host='%s', port=%d, status='%s'}",
                id, role, host, port, status);
    }
}
