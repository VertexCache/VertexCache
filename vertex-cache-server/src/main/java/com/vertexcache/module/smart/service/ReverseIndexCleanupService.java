package com.vertexcache.module.smart.service;

import com.vertexcache.core.cache.CacheAccessService;
import com.vertexcache.core.cache.exception.VertexCacheException;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;

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
public class ReverseIndexCleanupService extends BaseAlertService {

    private static final long SWEEP_INTERVAL_MS = 3_600_000;
    private static final Logger logger = Logger.getLogger(ReverseIndexCleanupService.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ReverseIndexCleanupService() throws VertexCacheException {
        super("ReverseIndexCleanupService", 0); // No scheduler needed
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::runSweepSafely, SWEEP_INTERVAL_MS, SWEEP_INTERVAL_MS, TimeUnit.MILLISECONDS);
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

    @Override
    protected void evaluate() throws VertexCacheTypeException {
        // NoOp
    }
}


