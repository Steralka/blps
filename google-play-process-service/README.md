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

## Запуск

1. Поднять PostgreSQL:

```bash
docker compose up -d
```

2. Запустить приложение:

```bash
mvn spring-boot:run
```

Сервис поднимется на `http://localhost:8080`.

## Основные REST API

### Каталог

- `GET /api/catalog/apps?query=&minPrice=&maxPrice` - поиск приложений.
- `GET /api/catalog/apps/{appId}` - карточка приложения.
- `POST /api/catalog/apps` - создание приложения (для тестовых данных).

### Платёжный аккаунт

- `POST /api/payment-accounts/users` - создать аккаунт.
- `GET /api/payment-accounts/users/{userId}` - получить аккаунт.
- `POST /api/payment-accounts/users/{userId}/top-up` - пополнить баланс.

### Карты

- `POST /api/cards` - добавить карту.
- `GET /api/cards?userId={userId}` - список активных карт.
- `DELETE /api/cards/{cardId}?userId={userId}` - удалить (деактивировать) карту.

### Установка/покупка

- `POST /api/installations` - установка приложения (и покупка для платного).
- `GET /api/installations?userId={userId}` - история установок.
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
