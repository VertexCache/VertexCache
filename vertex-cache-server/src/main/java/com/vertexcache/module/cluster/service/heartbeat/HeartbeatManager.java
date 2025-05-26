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
package com.vertexcache.module.cluster.service.heartbeat;

import com.vertexcache.common.log.LogHelper;
import com.vertexcache.module.cluster.ClusterModule;
import com.vertexcache.module.cluster.model.ClusterNode;
import com.vertexcache.module.cluster.service.coordination.FailoverManager;

import java.util.concurrent.*;

public class HeartbeatManager {

    private final static long INITIAL_DELAY = 10_000;

    private final ClusterModule clusterModule;
    private final int heartbeatIntervalMs;
    private final FailoverManager failoverManager;
    private final ScheduledExecutorService scheduler;

    private ScheduledFuture<?> scheduledTask;

    private boolean isMuted = true;

    public HeartbeatManager(ClusterModule clusterModule, int heartbeatIntervalMs) {
        this.clusterModule = clusterModule;
        this.heartbeatIntervalMs = heartbeatIntervalMs;
        this.failoverManager = new FailoverManager(clusterModule);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ClusterHeartbeatThread");
            t.setDaemon(true);
            return t;
        });
    }

    public void start() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) return;

        scheduledTask = scheduler.scheduleAtFixedRate(
                this::heartbeatLoop,
                INITIAL_DELAY,
                heartbeatIntervalMs,
                TimeUnit.MILLISECONDS
        );
    }

    public void shutdown() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(true);
        }
        scheduler.shutdownNow();
        LogHelper.getInstance().logInfo("[HeartbeatManager] Heartbeat loop stopped.");
    }

    private void heartbeatLoop() {
        try {
            ClusterNode target = null;

            if (clusterModule.getClusterConfig().isPrimaryNode()) {
                target = clusterModule.getClusterConfig().getSecondaryEnabledClusterNode();
            } else if (clusterModule.getClusterConfig().isSecondaryNode()) {
                target = clusterModule.getClusterConfig().getPrimaryEnabledClusterNode();
            }

            if (target != null) {
                LogHelper.getInstance().logInfo("[HeartbeatManager] Sending PEER_PING to node: " + target.getId());
                clusterModule.clusterPing(target);
            } else {
                LogHelper.getInstance().logInfo("[HeartbeatManager] No heartbeat target (likely standby/disabled).");
            }

            failoverManager.checkFailover();

        } catch (Exception e) {
            LogHelper.getInstance().logError("[HeartbeatManager] Heartbeat loop failed: " + e.getMessage());
        }
    }
}