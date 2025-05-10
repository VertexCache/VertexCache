package com.vertexcache.module.cluster.model;

public class ClusterNode {

    private String id;
    private String host;
    private String port;
    private ClusterNodeRole role;
    private ClusterNodeAvailability availability;
    private ClusterNodeHealthStatus healthStatus;
    private final ClusterNodeHeartBeat heartbeat = new ClusterNodeHeartBeat();

    private boolean promotedToPrimary;

    public ClusterNode(String id, String host, String port, ClusterNodeRole role, ClusterNodeAvailability availability) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.role = role;
        this.availability = availability;
    }

    public String getId() {
        return id;
    }
    public String getHost() {
        return host;
    }
    public String getPort() {return port;}
    public int getPortAsInt() { return Integer.parseInt(port); }
    public ClusterNodeRole getRole() {
        return role;
    }
    public ClusterNodeAvailability getAvailability() {return availability;}
    public ClusterNodeHealthStatus getHealthStatus() {return healthStatus;}
    public void setHealthStatus(ClusterNodeHealthStatus healthStatus) {this.healthStatus = healthStatus;}
    public ClusterNodeHeartBeat getHeartbeat() {
        return heartbeat;
    }
    public boolean isPromotedToPrimary() {return promotedToPrimary;}
    public void setPromotedToPrimary(boolean promotedToPrimary) {this.promotedToPrimary = promotedToPrimary;}
}
