package com.vertexcache.module.cluster.store;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClusterPeerStore {

    private final Map<String, PeerState> peerStates = new ConcurrentHashMap<>();
    private final List<PeerStateListener> listeners = new CopyOnWriteArrayList<>();

    public Optional<PeerState> get(String nodeId) {
        return Optional.ofNullable(peerStates.get(nodeId));
    }

    public void updateHeartbeat(String nodeId) {
        PeerState state = peerStates.computeIfAbsent(nodeId, PeerState::new);
        state.updateHeartbeat();
        notifyPeerUp(nodeId);
    }

    public void markPeerDown(String nodeId) {
        PeerState state = peerStates.get(nodeId);
        if (state != null && !state.isDown()) {
            state.markDown();
            notifyPeerDown(nodeId);
        }
    }

    public List<PeerState> list() {
        return new ArrayList<>(peerStates.values());
    }

    public void remove(String nodeId) {
        peerStates.remove(nodeId);
    }

    public void registerListener(PeerStateListener listener) {
        listeners.add(listener);
    }

    private void notifyPeerDown(String nodeId) {
        for (PeerStateListener listener : listeners) {
            listener.onPeerDown(nodeId);
        }
    }

    private void notifyPeerUp(String nodeId) {
        for (PeerStateListener listener : listeners) {
            listener.onPeerUp(nodeId);
        }
    }

    public void notifyRoleChange(String nodeId, String newRole) {
        for (PeerStateListener listener : listeners) {
            listener.onRoleChange(nodeId, newRole);
        }
    }
}