package com.vertexcache.module.cluster.model;

public class ClusterNode {

    private String id;
    private String host;
    private String port;
    private ClusterNodeRole role;
    private ClusterNodeAvailability availability;
    private ClusterNodeHealthStatus healthStatus;
    private final ClusterNodeHeartBeat heartbeat = new ClusterNodeHeartBeat();

    public ClusterNode(String id, String host, String port, ClusterNodeRole role, ClusterNodeAvailability availability, ClusterNodeHealthStatus healthStatus) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.role = role;
        this.availability = availability;
        this.healthStatus = healthStatus;
    }

    public String getId() {
        return id;
    }
    public String getHost() {
        return host;
    }
    public String getPort() {
        return port;
    }
    public ClusterNodeRole getRole() {
        return role;
    }
    public ClusterNodeAvailability getAvailability() {return availability;}
    public ClusterNodeHealthStatus getHealthStatus() {return healthStatus;}
    public void setHealthStatus(ClusterNodeHealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }
    public ClusterNodeHeartBeat getHeartbeat() {
        return heartbeat;
    }
}
