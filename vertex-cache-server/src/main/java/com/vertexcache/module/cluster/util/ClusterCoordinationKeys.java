package com.vertexcache.module.cluster.util;

import java.util.Set;

public class ClusterCoordinationKeys {
    public static final Set<String> ACTIVE_KEYS = Set.of(
            "cluster_failover_enabled",
            "cluster_failover_check_interval_ms",
            "cluster_failover_priority"
            /*
            "cluster_replication_retry_attempts",
            "cluster_replication_retry_interval_ms",
            "cluster_replication_queue_ttl_ms",
            "cluster_failover_priority",
            "cluster_config_strict",
            "cluster_advertise_port",
            "cluster_max_standbys",
            "cluster_max_standbys",
            "cluster_heartbeat_timeout_ms",
            "cluster_auto_rejoin_role"
             */
    );
}
