# Отчёт по Домашнему заданию №1

## Выполненные задачи

| Задача                              | Статус     | Пояснение                                                                                     |
|-------------------------------------|------------|-----------------------------------------------------------------------------------------------|
| Репозиторий на GitHub               | Выполнено  | [2026-MAI-Backend-A-Nedavniy](https://github.com/cucurbitapepo/2026-MAI-Backend-A-Nedavniy)   |
| Виртуальное окружение / зависимости | Выполнено  | [`build.gradle.kts`](../../build.gradle.kts)                                                  |
| Реализация LRU-cache                | Выполнено  | [`LRUCache.java`](../../src/main/java/com/mai/backend/cache/impl/LRUCache.java)               |
| Отдельные тесты                     | Выполнено  | [`LRUCacheTest.java`](../../src/test/java/cache/LRUCacheTest.java)                            |
| Осмысленные коммиты                 | Выполнено  | [История коммитов](https://github.com/cucurbitapepo/2026-MAI-Backend-A-Nedavniy/commits/main) |

---
## Реализация LRU-кэша

### Алгоритм
- **HashMap** для доступа к элементам за O(1)
- **Двусвязный список** для поддержания порядка использования
- Все операции (`get`, `set`, `remove`) работают за **O(1)**

### Интерфейс
```java
public interface Cache<K, V> {
    V get(K key);
    void set(K key, V value);
    void remove(K key);
}
```
## Инструкция по запуску
### Сборка и тесты
```bash
# Запустить все тесты
./gradlew test

# Собрать проект
./gradlew build

# Запустить приложение
./gradlew run
```