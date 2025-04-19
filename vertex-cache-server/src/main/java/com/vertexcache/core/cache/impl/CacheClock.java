package com.vertexcache.core.cache.impl;


import com.vertexcache.core.cache.CacheBase;
import com.vertexcache.core.cache.VertexCacheTypeException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * Clock algorithm used in the ClockCache efficiently manages the cache by distinguishing between hot and cold items and
 * selectively evicting less recently used items to make room for new entries. It provides a straightforward yet
 * effective approach to cache management, offering good performance in practice. The Clock algorithm, also known as
 * the Second Chance algorithm, is a page replacement algorithm used in computer operating systems and cache management.
 * It is based on the concept of a circular buffer or clock hand, which moves through the cache slots or page frames.
 *
 * Clock Hand:
 *  In the ClockCache, the clock hand (hand) is represented by an integer that tracks the current position in the cache.
 *  The clock hand moves circularly through the cache slots, visiting each slot in a sequential order.
 *
 * Hot and Cold Pages:
 *  Each cache slot is associated with a boolean flag (bit), called the clock bit, which indicates whether the
 *  corresponding item is "hot" (recently used) or "cold" (not recently used). Initially, all clock bits are set to
 *  false, indicating that all items are cold.
 *
 * Adding Items to the Cache (put method):
 *   When a new item is added to the cache, the clock hand moves forward, scanning each slot in the cache. If an empty
 *   slot is encountered, the new item is placed in that slot, and its clock bit is set to true, indicating that it is
 *   hot. If the clock bit of a slot is already true (indicating that the item is hot), the clock bit is reset to false,
 *   and the clock hand continues to move forward.
 *
 * Eviction Process:
 *   If the clock hand encounters a slot with a false clock bit (indicating a cold item), it marks that slot for
 *   eviction. The clock hand continues to move forward until it finds an empty slot or another hot item. If the cache
 *   is full and no empty slots are available, the eviction process stops when the clock hand returns to the original
 *   position. The item marked for eviction is removed from the cache, making room for the new item.
 *
 * Replacement Strategy:
 *   The Clock algorithm provides a form of least recently used (LRU) replacement strategy, where cold items are more
 *   likely to be evicted than hot items. By resetting the clock bit of hot items, the algorithm gives them a
 *   "second chance" to be retained in the cache, hence the name "Second Chance algorithm."
 *
 * Optimizations:
 *   The Clock algorithm provides a balance between simplicity and effectiveness, making it suitable for various cache
 *   management scenarios. Variants such as Clock-Pro introduce additional features for improved performance and
 *   adaptability to different workload patterns.
 *
 */
public class CacheClock<K, V> extends CacheBase<K, V> {

    private int sizeCapacity;
    private final boolean[] clockBits;
    private final ReentrantReadWriteLock lock;
    private int hand;

    public CacheClock(int sizeCapacity) {
        this.sizeCapacity = sizeCapacity;
        this.setPrimaryCache(new ConcurrentHashMap<>());
        this.clockBits = new boolean[sizeCapacity];
        this.lock = new ReentrantReadWriteLock();
        this.setSecondaryIndexOne(new ConcurrentHashMap<>());
        this.setSecondaryIndexTwo(new ConcurrentHashMap<>());
        this.hand = 0;
    }

    @Override
    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException {
        lock.writeLock().lock();
        try {
            if (this.getPrimaryCache().containsKey(primaryKey)) {
                // Update the existing entry
                this.getPrimaryCache().put(primaryKey, value);
            } else {
                // Evict if necessary
                while (true) {
                    if (!clockBits[hand]) {
                        K evictedKey = getKeyAtIndex(hand);
                        if (evictedKey != null) {
                            this.getPrimaryCache().remove(evictedKey);
                            clockBits[hand] = true;
                            this.getPrimaryCache().put(primaryKey, value);
                            this.updateSecondaryKeys(primaryKey,secondaryKeys);
                            return;
                        }
                    } else {
                        clockBits[hand] = false;
                    }

                    hand = (hand + 1) % sizeCapacity;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V get(K primaryKey) {
        return this.getDefaultImpl(primaryKey);
    }

    @Override
    public void remove(K primaryKey) {
        lock.writeLock().lock();
        try {
            this.getPrimaryCache().remove(primaryKey);
            this.getSecondaryIndexOne().remove(primaryKey);
            this.getSecondaryIndexTwo().remove(primaryKey);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private K getKeyAtIndex(int index) {
        for (Map.Entry<K, V> entry : this.getPrimaryCache().entrySet()) {
            if (entry.getValue() != null && entry.getKey().hashCode() % sizeCapacity == index) {
                return entry.getKey();
            }
        }
        return null;
    }

}
