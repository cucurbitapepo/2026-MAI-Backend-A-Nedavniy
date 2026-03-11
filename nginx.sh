#!/bin/bash
#
# nginx.sh - Скрипт управления nginx для проекта
# Использование: ./nginx.sh {start|stop|reload|restart|status|test}
#

set -e

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Пути
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"
NGINX_CONF="$PROJECT_ROOT/nginx/nginx.conf"
NGINX_LOGS="$PROJECT_ROOT/nginx/logs"

# Функции
print_info() {
    echo -e "${GREEN}◼${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}◼${NC} $1"
}

print_error() {
    echo -e "${RED}◼${NC} $1"
}

check_nginx() {
    if ! command -v nginx &> /dev/null; then
        print_error "nginx не установлен. Установите: sudo apt install nginx"
        exit 1
    fi
}

ensure_logs_dir() {
    if [ ! -d "$NGINX_LOGS" ]; then
        mkdir -p "$NGINX_LOGS"
        print_info "Создана папка для логов: $NGINX_LOGS"
    fi
}

start_nginx() {
    check_nginx
    ensure_logs_dir

    print_info "Тестирование конфигурации..."
    sudo nginx -t -p "$PROJECT_ROOT" -c "$NGINX_CONF"

    print_info "Запуск nginx..."
    sudo nginx -p "$PROJECT_ROOT" -c "$NGINX_CONF"

    print_info "nginx запущен!"
    echo ""
    echo "Проверка:"
    echo "  - Статика: http://localhost/public/"
    echo "  - API:     http://localhost/api/password"
    echo "  - Логи:    $NGINX_LOGS/access.log"
}

stop_nginx() {
    check_nginx

    if [ -f "$NGINX_LOGS/nginx.pid" ]; then
        print_info "Остановка nginx..."
        sudo nginx -s stop -p "$PROJECT_ROOT" -c "$NGINX_CONF"
        print_info "nginx остановлен"
    else
        print_warn "PID файл не найден. Возможно, nginx не запущен."
        print_warn "Попробуйте: sudo pkill nginx"
    fi
}

reload_nginx() {
    check_nginx

    print_info "Тестирование конфигурации..."
    sudo nginx -t -p "$PROJECT_ROOT" -c "$NGINX_CONF"

    print_info "Перезагрузка конфигурации..."
    sudo nginx -s reload -p "$PROJECT_ROOT" -c "$NGINX_CONF"
    print_info "Конфигурация перезагружена"
}

restart_nginx() {
    stop_nginx
    sleep 1
    start_nginx
}

status_nginx() {
    check_nginx

    echo "=== nginx status ==="
    if pgrep -x "nginx" > /dev/null; then
        print_info "nginx запущен"
        echo ""
        echo "Процессы:"
        ps aux | grep "[n]ginx" | head -5
        echo ""
        if [ -f "$NGINX_LOGS/nginx.pid" ]; then
            echo "PID: $(cat "$NGINX_LOGS/nginx.pid")"
        fi
    else
        print_warn "nginx не запущен"
    fi
}

test_nginx() {
    check_nginx
    ensure_logs_dir

    print_info "Тестирование конфигурации..."
    sudo nginx -t -p "$PROJECT_ROOT" -c "$NGINX_CONF"
}

# Главная логика
case "${1:-}" in
    start)
        start_nginx
        ;;
    stop)
        stop_nginx
        ;;
    reload)
        reload_nginx
        ;;
    restart)
        restart_nginx
        ;;
    status)
        status_nginx
        ;;
    test)
        test_nginx
        ;;
    *)
        echo "Использование: $0 {start|stop|reload|restart|status|test}"
        echo ""
        echo "Команды:"
        echo "  start   - Запустить nginx"
        echo "  stop    - Остановить nginx"
        echo "  reload  - Перезагрузить конфигурацию"
        echo "  restart - Перезапустить nginx"
        echo "  status  - Показать статус nginx"
        echo "  test    - Протестировать конфигурацию"
        exit 1
        ;;
esac