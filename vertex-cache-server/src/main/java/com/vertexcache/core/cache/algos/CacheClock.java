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
package com.vertexcache.core.cache.algos;

import com.vertexcache.core.cache.CacheBase;
import com.vertexcache.core.cache.exception.VertexCacheTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private final List<K> keyRing = new ArrayList<>();
    private int sizeCapacity;
    private Map<K, Boolean> clockBits = new HashMap<>();
    private final ReentrantReadWriteLock lock;
    private int hand;

    public CacheClock(int sizeCapacity) {
        this.sizeCapacity = sizeCapacity;
        this.setPrimaryCache(new ConcurrentHashMap<>());
        this.clockBits = new HashMap<>();
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
                // Update existing key; refresh value only
                this.getPrimaryCache().put(primaryKey, value);
                return;
            }

            // Eviction check

            if (this.getPrimaryCache().size() >= this.sizeCapacity) {
                int attempts = 0;
                while (attempts < sizeCapacity * 2) {
                    if (keyRing.isEmpty()) {
                        throw new VertexCacheTypeException("Clock put() failed: keyRing unexpectedly empty.");
                    }

                    K candidateKey = keyRing.get(hand);
                    Boolean bit = clockBits.getOrDefault(candidateKey, false);

                    if (!bit) {
                        this.getPrimaryCache().remove(candidateKey);
                        this.cleanupIndexFor(candidateKey);
                        clockBits.remove(candidateKey);
                        keyRing.remove(hand);
                        if (hand >= keyRing.size()) hand = 0;
                        break;
                    } else {
                        clockBits.put(candidateKey, false);
                        hand = (hand + 1) % keyRing.size();
                    }
                    attempts++;
                }

                if (attempts >= sizeCapacity * 2) {
                    throw new VertexCacheTypeException("Clock put() failed to find eviction candidate after full rotation.");
                }
            }

            keyRing.add(primaryKey);
            clockBits.put(primaryKey, true);
            this.putDefaultImpl(primaryKey, value, secondaryKeys);

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
            this.cleanupIndexFor(primaryKey);
        } finally {
            lock.writeLock().unlock();
        }
    }
}