package com.vertexcache.module.smart.service;

import com.vertexcache.core.cache.CacheAccessService;
import com.vertexcache.core.cache.exception.VertexCacheException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
/**
 * ReverseIndexSweeperService is a SmartModule-level background thread
 * that periodically triggers the sweep logic via the CacheAccessService.
 *
 * It delegates to the cache subsystem through a clean interface without
 * directly referencing internal cache details or data structures.
 */
public class ReverseIndexSweeperService {

    private static final Logger logger = Logger.getLogger(ReverseIndexSweeperService.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final long sweepIntervalMs;

    public ReverseIndexSweeperService(long sweepIntervalMs) throws VertexCacheException {
        this.sweepIntervalMs = sweepIntervalMs;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::runSweepSafely, sweepIntervalMs, sweepIntervalMs, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    private void runSweepSafely() {
        try {
            logger.fine("[ReverseIndexSweeper] Starting sweep");
            CacheAccessService service = new CacheAccessService();
            service.sweepOrphanedIndexEntries();
            logger.fine("[ReverseIndexSweeper] Sweep complete");
        } catch (Exception e) {
            logger.warning("[ReverseIndexSweeper] Sweep failed: " + e.getMessage());
        }
    }
}


