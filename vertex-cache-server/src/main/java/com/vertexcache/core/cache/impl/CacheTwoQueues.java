package com.vertexcache.core.cache.impl;

import com.vertexcache.core.cache.CacheBase;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * Two Queues (2Q) caching strategy, two separate queues are utilized: the "in" queue (inQueue) and the "out"
 * queue (outQueue). These queues serve different purposes and help in managing the cache efficiently.
 *
 * In Queue (inQueue):
 *   The inQueue represents the "hot" or frequently accessed items. When an item is accessed or added to the
 *   cache (via the put method), it's moved to the front of the inQueue. This signifies that the item is recently
 *   used and considered "hot". Items in the inQueue are the most likely candidates for retention in the cache, as
 *   they are recently accessed and have higher chances of being accessed again.
 *
 * Out Queue (outQueue):
 *   The outQueue represents the "cold" or less frequently accessed items. Items in the outQueue are less likely to be
 *   accessed again soon. When the cache reaches its capacity and needs to evict an item, it removes the least recently
 *   used item from the outQueue.
 *
 * Cache Management:
 *   When adding a new item to the cache (put method), it's initially added to the inQueue. If the cache is full, and
 *   a new item needs to be added, the least recently used item from the outQueue is evicted to make space.
 *   If an existing item is accessed again (get method), it's moved to the front of the inQueue, indicating its recent
 *   usage. This strategy ensures that frequently accessed items remain in the cache (inQueue) while less frequently
 *   accessed items may eventually be evicted from the cache (outQueue).
 *
 * Cache Eviction:
 *   When the cache reaches its capacity, and a new item needs to be added, the least recently used item from the
 *   outQueue is evicted. This ensures that the cache size does not exceed its specified capacity.
 */
public class CacheTwoQueues<K, V> extends CacheBase<K, V> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<K, Boolean> inQueue; // LinkedHashMap for new elements
    private final Map<K, Boolean> outQueue; // LinkedHashMap for old elements
    private int sizeCapacity;

    public CacheTwoQueues(int sizeCapacity) {
        this.sizeCapacity = sizeCapacity;
        this.inQueue = Collections.synchronizedMap(new LinkedHashMap<>());
        this.outQueue = Collections.synchronizedMap(new LinkedHashMap<>());
        this.setPrimaryCache(Collections.synchronizedMap(new HashMap<>()));
        this.setSecondaryIndexOne(Collections.synchronizedMap(new HashMap<>()));
        this.setSecondaryIndexTwo(Collections.synchronizedMap(new HashMap<>()));
    }

    @Override
    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException {
        lock.writeLock().lock();
        try {
            if (this.getPrimaryCache().containsKey(primaryKey)) {
                inQueue.remove(primaryKey);
                inQueue.put(primaryKey, true);
                this.getPrimaryCache().put(primaryKey, value);
            } else {
                while (this.getPrimaryCache().size() >= sizeCapacity) {
                    Iterator<Map.Entry<K, Boolean>> iterator = outQueue.entrySet().iterator();
                    if (iterator.hasNext()) {
                        K outKey = iterator.next().getKey();
                        iterator.remove();
                        this.getPrimaryCache().remove(outKey);
                        this.cleanupIndexFor(outKey);
                    } else {
                        // fallback: remove from inQueue
                        Iterator<Map.Entry<K, Boolean>> inIterator = inQueue.entrySet().iterator();
                        if (inIterator.hasNext()) {
                            K inKey = inIterator.next().getKey();
                            inIterator.remove();
                            this.getPrimaryCache().remove(inKey);
                            this.cleanupIndexFor(inKey);
                        } else {
                            break; // nothing more to evict
                        }
                    }
                }
                inQueue.put(primaryKey, true);
                this.getPrimaryCache().put(primaryKey, value);
            }
            this.updateSecondaryKeys(primaryKey, secondaryKeys);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V get(K key) {
        synchronized (this.getPrimaryCache()) {
            if (this.getPrimaryCache().containsKey(key)) {
                // Move the key to the front of the inQueue
                inQueue.remove(key);
                inQueue.put(key, true);
                return this.getPrimaryCache().get(key);
            }
            return null;
        }
    }

    @Override
    public void remove(K key) {
        synchronized (this.getPrimaryCache()) {
            if (this.getPrimaryCache().containsKey(key)) {
                this.getPrimaryCache().remove(key);
                inQueue.remove(key);
                this.cleanupIndexFor(key);
            }
        }
    }
}
