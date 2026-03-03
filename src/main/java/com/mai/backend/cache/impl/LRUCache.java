package com.mai.backend.cache.impl;

import com.mai.backend.cache.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * LRU (Least Recently Used) cache implementation.
 * <p>
 * Provides O(1) time complexity for {@code get}, {@code set}, and {@code remove}
 * operations using a combination of HashMap and doubly-linked list.
 * </p>
 * <p>
 * <b>Thread safety:</b> This implementation is NOT thread-safe.
 * For concurrent access, consider external synchronization.
 */
public class LRUCache implements Cache<String, String> {

  private final int capacity;
  private final Map<String, Node> cache;
  private final Node head;
  private final Node tail;

  private static class Node {
    final String key;
    String value;
    Node prev;
    Node next;

    Node(String key, String value) {
      this.key = key;
      this.value = value;
    }
  }

  /**
   * Constructs a new LRU cache with specified capacity.
   *
   * @param capacity maximum number of entries; must be positive
   * @throws IllegalArgumentException if capacity is less than or equal to zero
   */
  public LRUCache(final int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be greater than 0");
    }

    this.capacity = capacity;
    this.cache = new HashMap<>(capacity);
    this.head = new Node(null, null);
    this.tail = new Node(null, null);
    head.next = tail;
    tail.prev = head;
  }

  /**
   * Retrieves value associated with the given key.
   * <p>
   * If the key exists, it becomes the most recently used entry.
   * </p>
   *
   * @param key the key to look up
   * @return the associated value, or null if key not found
   * @throws NullPointerException if key is null
   */
  @Override
  public String get(String key) {
    Objects.requireNonNull(key, "Key cannot be null");
    Node node = cache.get(key);
    if (node == null) {
      return null;
    }
    moveToHead(node);
    return node.value;
  }

  /**
   * Puts a key-value pair into the cache.
   * <p>
   * If the key already exists, updates the value and marks it as most recently used.
   * If cache exceeds capacity after insertion, the least recently used entry is evicted.
   * </p>
   *
   * @param key   the key to store
   * @param value the value to store
   * @throws NullPointerException if key is null
   */
  @Override
  public void set(String key, String value) {
    Objects.requireNonNull(key, "Key cannot be null");

    Node node = cache.get(key);
    if (node != null) {
      node.value = value;
      moveToHead(node);
    } else {
      Node newNode = new Node(key, value);
      cache.put(key, newNode);
      addToHead(newNode);

      if (cache.size() > capacity) {
        Node removed = removeTail();
        cache.remove(removed.key);
      }
    }

  }

  /**
   * Removes entry associated with the given key.
   * <p>
   * Does nothing if key is not present in the cache.
   * </p>
   *
   * @param key the key to remove (ignored if null)
   */
  @Override
  public void remove(String key) {
    if (key == null) return;
    Node node = cache.get(key);
    if (node != null) {
      removeNode(node);
      cache.remove(key);
    }
  }

  private void addToHead(Node node) {
    node.prev = head;
    node.next = head.next;
    head.next.prev = node;
    head.next = node;
  }

  private void removeNode(Node node) {
    node.prev.next = node.next;
    node.next.prev = node.prev;
  }

  private void moveToHead(Node node) {
    removeNode(node);
    addToHead(node);
  }

  private Node removeTail() {
    Node node = tail.prev;
    removeNode(node);
    return node;
  }
}
