# Google Play Process Service

Spring Boot приложение для реализации бизнес-процесса Google Play:
- взаимодействие с каталогом (поиск, выбор);
- установка и/или покупка приложения;
- управление картами и платёжными аккаунтами.

Хранилище данных: PostgreSQL.
Все публичные интерфейсы реализованы через REST API.

## Технологии

- Java 17
- Spring Boot 3.3.2
- Spring Web, Spring Data JPA, Bean Validation
- PostgreSQL
- Flyway
- Springdoc OpenAPI / Swagger UI
- Docker, Docker Compose

## BPMN

Модель бизнес-процесса BPMN 2.0 находится в `src/main/resources/bpmn/google-play-process.bpmn`.

## Запуск

### Через Docker Compose

```bash
docker compose up --build
```

Сервис поднимется на `http://localhost:8080`, PostgreSQL - на `localhost:5432`.

Если порт `8080` уже занят, можно запустить на другом порту хоста:

```bash
APP_HOST_PORT=8081 docker compose up --build
```

Тогда сервис будет доступен по `http://localhost:8081`.

Если занят и `5432`, задайте оба порта:

```bash
APP_HOST_PORT=8081 POSTGRES_HOST_PORT=5433 docker compose up --build
```

Тогда PostgreSQL будет доступен на `localhost:5433`.

### Локально

1. Поднять PostgreSQL:

```bash
docker compose up -d postgres
```

2. Запустить приложение:

```bash
mvn spring-boot:run
```

Сервис поднимется на `http://localhost:8080`.

## Swagger

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Основные REST API

### Каталог

- `GET /api/catalog/apps?query=&minPrice=&maxPrice` - поиск приложений.
- `GET /api/catalog/apps/{appId}` - карточка приложения.
- `POST /api/catalog/apps` - создание приложения (для тестовых данных).
- `PUT /api/catalog/apps/{appId}` - обновление приложения.
- `DELETE /api/catalog/apps/{appId}` - удаление приложения из активного каталога.

### Платёжный аккаунт

- `POST /api/payment-accounts/users` - создать аккаунт.
- `GET /api/payment-accounts/users/{userId}` - получить аккаунт.
- `PUT /api/payment-accounts/users/{userId}` - обновить email и имя.
- `DELETE /api/payment-accounts/users/{userId}` - деактивировать аккаунт.
- `POST /api/payment-accounts/users/{userId}/top-up` - пополнить баланс.

### Карты

- `POST /api/cards` - добавить карту.
- `GET /api/cards?userId={userId}` - список активных карт.
- `PUT /api/cards/{cardId}?userId={userId}` - обновить держателя и срок действия карты.
- `DELETE /api/cards/{cardId}?userId={userId}` - удалить (деактивировать) карту.

### Установка/покупка

- `POST /api/installations` - установка приложения (и покупка для платного).
- `GET /api/installations?userId={userId}` - история установок.
- `DELETE /api/installations/{installationId}?userId={userId}` - удалить установленное приложение.
- `GET /api/purchases?userId={userId}` - история покупок.

## Тестирование API

- Готовый curl-сценарий: `scripts/curl-demo.sh`
- Экспорт коллекции Insomnia: `insomnia/google-play-api-insomnia.json`

## Пример тела запроса

Создание платежного аккаунта:

```json
{
  "email": "student@example.com",
  "displayName": "Student",
  "initialBalance": 100.00
}
```

Установка (и покупка) приложения:

```json
{
  "userId": 1,
  "appId": 2,
  "cardId": 1
}
```

## Подготовка к Helios

На сервере с Docker достаточно скопировать проект, перейти в каталог `google-play-process-service` и выполнить:

```bash
docker compose up --build -d
```

При необходимости переопределить параметры базы можно через переменные `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `SERVER_PORT` в `docker-compose.yml` или окружении сервера.
