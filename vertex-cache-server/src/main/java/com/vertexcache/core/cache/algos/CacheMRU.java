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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * MRU (Most Recently Used) eviction policy with full O(1) access and eviction.
 * Maintains internal access-order using a doubly-linked list and a map for node lookups.
 */
public class CacheMRU<K, V> extends CacheBase<K, V> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<K, Node<K>> nodeMap = new HashMap<>();
    private final DoublyLinkedList<K> accessList = new DoublyLinkedList<>();
    private final int sizeCapacity;

    public CacheMRU(int sizeCapacity) {
        this.sizeCapacity = sizeCapacity;
        this.setPrimaryCache(Collections.synchronizedMap(new HashMap<>()));
    }

    @Override
    public void put(K primaryKey, V value, Object... secondaryKeys) throws VertexCacheTypeException {
        lock.writeLock().lock();
        try {
            if (this.getPrimaryCache().size() >= this.sizeCapacity) {
                K mruKey = accessList.getHeadKey();
                if (mruKey != null) {
                    this.getPrimaryCache().remove(mruKey);
                    this.cleanupIndexFor(mruKey);
                    accessList.removeHead();
                    nodeMap.remove(mruKey);
                }
            }

            boolean isUpdate = this.containsKey(primaryKey);
            this.putDefaultImpl(primaryKey, value, secondaryKeys);

            if (isUpdate) {
                Node<K> node = nodeMap.get(primaryKey);
                if (node != null) {
                    accessList.moveToFront(node);
                }
            } else {
                Node<K> node = new Node<>(primaryKey);
                accessList.addFirst(node);
                nodeMap.put(primaryKey, node);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V get(K primaryKey) {
        lock.writeLock().lock();
        try {
            V value = this.getDefaultImpl(primaryKey);
            if (value != null) {
                Node<K> node = nodeMap.get(primaryKey);
                if (node != null) {
                    accessList.moveToFront(node);
                }
            }
            return value;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void remove(K primaryKey) {
        lock.writeLock().lock();
        try {
            this.removeDefaultImpl(primaryKey);
            Node<K> node = nodeMap.remove(primaryKey);
            if (node != null) {
                accessList.remove(node);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V getBySecondaryKeyIndexOne(Object secondaryKey) {
        lock.writeLock().lock();
        try {
            if (secondaryKey != null && this.getSecondaryIndexOne().containsKey(secondaryKey)) {
                K key = this.getSecondaryIndexOne().get(secondaryKey);
                V value = this.getPrimaryCache().get(key);
                if (value != null) {
                    Node<K> node = nodeMap.get(key);
                    if (node != null) {
                        accessList.moveToFront(node);
                    }
                }
                return value;
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V getBySecondaryKeyIndexTwo(Object secondaryKey) {
        lock.writeLock().lock();
        try {
            if (secondaryKey != null && this.getSecondaryIndexTwo().containsKey(secondaryKey)) {
                K key = this.getSecondaryIndexTwo().get(secondaryKey);
                V value = this.getPrimaryCache().get(key);
                if (value != null) {
                    Node<K> node = nodeMap.get(key);
                    if (node != null) {
                        accessList.moveToFront(node);
                    }
                }
                return value;
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ==== Internal Doubly Linked List ====

    private static class Node<K> {
        final K key;
        Node<K> prev;
        Node<K> next;

        Node(K key) {
            this.key = key;
        }
    }

    private static class DoublyLinkedList<K> {
        private Node<K> head;
        private Node<K> tail;

        void addFirst(Node<K> node) {
            node.next = head;
            if (head != null) head.prev = node;
            head = node;
            if (tail == null) tail = head;
        }

        void moveToFront(Node<K> node) {
            if (node == head) return;
            remove(node);
            addFirst(node);
        }

        void remove(Node<K> node) {
            if (node.prev != null) node.prev.next = node.next;
            if (node.next != null) node.next.prev = node.prev;
            if (node == head) head = node.next;
            if (node == tail) tail = node.prev;
            node.prev = node.next = null;
        }

        void removeHead() {
            if (head != null) {
                Node<K> next = head.next;
                if (next != null) next.prev = null;
                if (head == tail) tail = null;
                head = next;
            }
        }

        K getHeadKey() {
            return head != null ? head.key : null;
        }
    }
}
