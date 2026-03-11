# Отчёт по Домашнему заданию №2

## Выполненные задачи
| Задача                                 | Статус    | Пояснение                                                                                           |
|----------------------------------------|-----------|-----------------------------------------------------------------------------------------------------|
| Установка nginx                        | Выполнено | `sudo apt install nginx`                                                                            |
| Настройка статики (`public/`)          | Выполнено | [`nginx.conf`](../../nginx/nginx.conf), `location /public/`                                         |
| Java-приложение (аналог WSGI/gunicorn) | Выполнено | [`PasswordController.java`](../../src/main/java/com/mai/backend/controller/PasswordController.java) |
| Проксирование через upstream           | Выполнено | `upstream java_app` + `proxy_pass` в nginx.conf                                                     |
| Замер производительности (ab)          | Выполнено | [`PERFORMANCE.md`](PERFORMANCE.md)                                                                  |
| Портативный запуск                     | Выполнено | [`nginx.sh`](../../nginx.sh)                                                                        |

---

## Архитектура решения
```
┌─────────────┐         ┌─────────────┐      ┌─────────────┐
│   Клиент    │ ──────→ │   nginx     │ ───→ │ Spring Boot │
│  (Browser,  │ HTTP:80 │  (Reverse   │      │  (Tomcat)   │
│    curl)    │         │   Proxy)    │      │   :8080     │
└─────────────┘         └─────────────┘      └─────────────┘
                               │
                               ↓
                        ┌─────────────┐
                        │ public/     │
                        │ (static)    │
                        └─────────────┘
```


### Компоненты

| Компонент       | Назначение                                 | Файл                      |
|-----------------|--------------------------------------------|---------------------------|
| **nginx**       | Обратный прокси, отдача статики            | `nginx/nginx.conf`        |
| **Spring Boot** | Генерация паролей (аналог WSGI-приложения) | `PasswordController.java` |
| **public/**     | Статические файлы                          | `public/test.txt`         |
| **nginx.sh**    | Скрипт управления                          | `nginx.sh`                |

---
## Инструкция по запуску
### 0. Требования
* Java 17+
* Gradle 8.14+
* nginx 1.18+

### 1. Запуск Java-приложения
```bash
# Сборка
./gradlew bootJar

# Запуск (в фоновом режиме)
java -jar build/libs/2026-MAI-Backend-A-Nedavniy-1.0-SNAPSHOT.jar &

# Проверка
curl http://localhost:8080/api/password
```
### 2. Управление nginx
```bash
# Первый раз: сделать скрипт исполняемым
chmod +x nginx.sh

# Запустить nginx
./nginx.sh start

# Проверить статус
./nginx.sh status

# Перезагрузить конфигурацию (после изменений)
./nginx.sh reload

# Протестировать конфигурацию
./nginx.sh test

# Остановить nginx
./nginx.sh stop
```

### 3. Проверка работы
```bash
# Статические файлы
curl http://localhost/public/
curl http://localhost/public/test.txt

# API (генерация пароля)
curl http://localhost/api/password
curl http://localhost/api/password?length=12
```

### 4. Тестирование производительности
```bash
# Установка ab (если не установлен)
sudo apt install apache2-utils

# Базовый тест
ab -n 1000 -c 10 http://localhost/api/password

# Подбор максимального RPS
ab -n 10000 -c 50 http://localhost/api/password
ab -n 10000 -c 100 http://localhost/api/password
ab -n 10000 -c 200 http://localhost/api/password
```

### 5. Просмотр логов
```bash
# Логи доступа
tail -f nginx/logs/access.log

# Логи ошибок
tail -f nginx/logs/error.log
```

### 6. Остановка всего
```bash
# Остановить nginx
./nginx.sh stop

# Остановить Java-приложение
sudo pkill -f "2026-MAI-Backend-A-Nedavniy-1.0-SNAPSHOT.jar"
```