# Google Play Process Service (Lab #2)

## Аутентификация (JWT) и JAAS

1. Пользователи хранятся в `src/main/resources/security/users.xml`.
2. Логин: `POST /api/auth/login`
   - тело: `{"username":"admin","password":"admin123"}`
   - ответ: `{"token":"<jwt>"}`
3. JWT: `Authorization: Bearer <jwt>`

## Роли и привилегии (модель доступа)

Роли:
- `ROLE_ADMIN`
- `ROLE_USER`

Привилегии:
- `PRIV_CATALOG_READ`, `PRIV_CATALOG_WRITE`
- `PRIV_ACCOUNT_READ_SELF`, `PRIV_ACCOUNT_WRITE_SELF`, `PRIV_ACCOUNT_ADMIN`
- `PRIV_CARD_MANAGE_SELF`, `PRIV_CARD_ADMIN`
- `PRIV_INSTALL_SELF`, `PRIV_INSTALL_ADMIN`

Связка ролей и привилегий задаётся в `src/main/java/ru/blps/googleplay/security/RolePrivileges.java`.

Политика владения (для ролей без `*_ADMIN`):
- операции с `userId` разрешены только если `UserAccount.email == username` из JWT.

## Транзакции (Spring JTA + Narayana)

Транзакции выполняются программно через `ru.blps.googleplay.tx.TxExecutor` (без `@Transactional`).
Менеджер транзакций — Narayana (`org.jboss.narayana.jta:narayana-jta`), бин `JtaTransactionManager` в `ru.blps.googleplay.config.JtaNarayanaConfig`.

## Скрипты

`scripts/curl-demo.sh` выполняет демонстрационный сценарий и автоматически логинится как `admin`.

