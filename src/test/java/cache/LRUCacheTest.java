package cache;

import com.mai.backend.cache.Cache;
import com.mai.backend.cache.impl.LRUCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LRUCache Tests")
class LRUCacheTest {

  private LRUCache cache;

  @BeforeEach
  @DisplayName("Initialize cache with capacity 3")
  void setUp() {
    cache = new LRUCache(3);
  }

  // ═══════════════════════════════════════════════════════════════
  // Basic Operations
  // ═══════════════════════════════════════════════════════════════
  @Nested
  @DisplayName("Basic Operations")
  class BasicOperations {

    @Test
    @DisplayName("Get non-existent key returns null")
    void getNonExistentKey() {
      assertNull(cache.get("nonexistent"));
    }

    @Test
    @DisplayName("Set and get single value")
    void setAndGetSingleValue() {
      cache.set("key1", "value1");
      assertEquals("value1", cache.get("key1"));
    }

    @Test
    @DisplayName("Update existing key")
    void updateExistingKey() {
      cache.set("key1", "value1");
      cache.set("key1", "value2");
      assertEquals("value2", cache.get("key1"));
    }

    @Test
    @DisplayName("Remove key")
    void removeKey() {
      cache.set("key1", "value1");
      cache.remove("key1");
      assertNull(cache.get("key1"));
    }
  }

  // ═══════════════════════════════════════════════════════════════
  // LRU Eviction Logic
  // ═══════════════════════════════════════════════════════════════
  @Nested
  @DisplayName("LRU Eviction Logic")
  class EvictionLogic {

    @Test
    @DisplayName("Evict least recently used when capacity exceeded")
    void lruEviction() {
      cache.set("key1", "value1");
      cache.set("key2", "value2");
      cache.set("key3", "value3");
      cache.set("key4", "value4"); // evicts key1

      assertNull(cache.get("key1")); // evicted
      assertEquals("value2", cache.get("key2"));
      assertEquals("value3", cache.get("key3"));
      assertEquals("value4", cache.get("key4"));
    }

    @Test
    @DisplayName("Access updates LRU order")
    void accessUpdatesOrder() {
      cache.set("key1", "value1");
      cache.set("key2", "value2");
      cache.set("key3", "value3");
      cache.get("key1"); // access key1, making it MRU
      cache.set("key4", "value4"); // evicts key2 (now LRU)

      assertEquals("value1", cache.get("key1"));
      assertNull(cache.get("key2")); // evicted
      assertEquals("value3", cache.get("key3"));
      assertEquals("value4", cache.get("key4"));
    }

    @Test
    @DisplayName("Multiple evictions in sequence")
    void multipleEvictions() {
      cache.set("a", "1");
      cache.set("b", "2");
      cache.set("c", "3");
      cache.get("a"); // a becomes MRU
      cache.set("d", "4"); // evicts b
      cache.set("e", "5"); // evicts c

      assertEquals("1", cache.get("a"));
      assertNull(cache.get("b"));
      assertNull(cache.get("c"));
      assertEquals("4", cache.get("d"));
      assertEquals("5", cache.get("e"));
    }
  }

  // ═══════════════════════════════════════════════════════════════
  // Task Requirements
  // ═══════════════════════════════════════════════════════════════
  @Nested
  @DisplayName("Task Requirements")
  class TaskRequirements {

    @Test
    @DisplayName("Example from task description")
    void taskExample() {
      LRUCache taskCache = new LRUCache(100);
      taskCache.set("Jesse", "Pinkman");
      taskCache.set("Walter", "White");
      taskCache.set("Jesse", "James");

      assertEquals("James", taskCache.get("Jesse"));
      taskCache.remove("Walter");
      assertNull(taskCache.get("Walter"));
    }

    @Test
    @DisplayName("Cache with capacity 2")
    void cacheWithCapacityTwo() {
      Cache<String, String> cacheForTwo = new LRUCache(2);

      cacheForTwo.set("a", "1");
      cacheForTwo.set("b", "2");
      assertEquals("1", cacheForTwo.get("a"));
      assertEquals("2", cacheForTwo.get("b"));

      cacheForTwo.set("c", "3");
      assertNull(cacheForTwo.get("a")); // evicted
    }
  }

  // ═══════════════════════════════════════════════════════════════
  // Edge Cases & Validation
  // ═══════════════════════════════════════════════════════════════
  @Nested
  @DisplayName("Edge Cases & Validation")
  class EdgeCases {

    @Test
    @DisplayName("Null key handling")
    void nullKeyHandling() {
      assertThrows(NullPointerException.class, () -> cache.set(null, "value"));
    }

    @Test
    @DisplayName("Null value handling - graceful degradation")
    void nullValueHandling() {
      cache.set("key", null);
      assertNull(cache.get("key"));
    }

    @Test
    @DisplayName("Capacity validation - zero capacity")
    void zeroCapacityThrows() {
      assertThrows(IllegalArgumentException.class, () -> new LRUCache(0));
    }

    @Test
    @DisplayName("Capacity validation - negative capacity")
    void negativeCapacityThrows() {
      assertThrows(IllegalArgumentException.class, () -> new LRUCache(-1));
    }

    @Test
    @DisplayName("Capacity of 1 works correctly")
    void capacityOne() {
      LRUCache singleCache = new LRUCache(1);
      singleCache.set("key1", "value1");
      assertEquals("value1", singleCache.get("key1"));
      singleCache.set("key2", "value2");
      assertNull(singleCache.get("key1")); // evicted
      assertEquals("value2", singleCache.get("key2"));
    }

    @Test
    @DisplayName("Empty string as key and value")
    void emptyString() {
      cache.set("", "");
      assertEquals("", cache.get(""));
    }
  }

  // ═══════════════════════════════════════════════════════════════
  // Interface Contract
  // ═══════════════════════════════════════════════════════════════
  @Nested
  @DisplayName("Interface Contract")
  class InterfaceContract {

    @Test
    @DisplayName("Cache interface polymorphism")
    void interfacePolymorphism() {
      Cache<String, String> cacheInterface = new LRUCache(5);
      cacheInterface.set("key", "value");
      assertEquals("value", cacheInterface.get("key"));
      cacheInterface.remove("key");
      assertNull(cacheInterface.get("key"));
    }
  }
}