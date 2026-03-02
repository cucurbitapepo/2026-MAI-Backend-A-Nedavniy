package com.mai.backend.cache;

/**
 * Generic cache interface with basic operations.
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface Cache<K, V> {

  /**
   * Retrieves value by key.
   *
   * @param key the key to look up
   * @return value if found, null otherwise
   */
  V get(K key);

  /**
   * Puts key-value pair into cache.
   * Updates access order for LRU policy.
   */
  void set(K key, V value);

  /**
   * Removes entry by key.
   */
  void remove(K key);

}
