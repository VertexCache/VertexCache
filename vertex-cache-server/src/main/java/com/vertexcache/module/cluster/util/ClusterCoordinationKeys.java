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
