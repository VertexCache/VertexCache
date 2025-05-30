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
package com.vertexcache.module.cluster.model;

/**
 * ClusterNodeHeartBeat represents a periodic heartbeat signal sent by a node in the VertexCache cluster.
 * It contains metadata such as the node ID, current role, config hash, and timestamp,
 * allowing peers to assess liveness, configuration alignment, and role continuity.
 *
 * This class is a key component of cluster health monitoring and is used in determining
 * node availability, triggering failover, and maintaining cluster consistency.
 */
public class ClusterNodeHeartBeat {

    private volatile long lastHeartbeatTime;
    private volatile boolean down;

    public ClusterNodeHeartBeat() {
        this.lastHeartbeatTime = System.currentTimeMillis();
        this.down = false;
    }

    public long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public boolean isDown() {
        return down;
    }

    public void updateHeartbeat() {
        this.lastHeartbeatTime = System.currentTimeMillis();
        this.down = false;
    }

    public void markDown() {
        this.down = true;
    }

    public boolean isAlive(long timeoutMs) {
        return !down && (System.currentTimeMillis() - lastHeartbeatTime) <= timeoutMs;
    }
}
