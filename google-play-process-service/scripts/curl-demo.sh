#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
RUN_ID="$(date +%s)"

extract_id() {
  sed -n 's/.*"id":\([0-9]*\).*/\1/p'
}

echo "1) Создание платежного аккаунта"
USER_ID=$(curl -sS -X POST "$BASE_URL/api/payment-accounts/users" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"student-$RUN_ID@example.com\",\"displayName\":\"Student\",\"initialBalance\":100.00}" | extract_id)
echo "USER_ID=$USER_ID"

echo "2) Обновление платежного аккаунта"
curl -sS -X PUT "$BASE_URL/api/payment-accounts/users/$USER_ID" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"student-updated-$RUN_ID@example.com\",\"displayName\":\"Updated Student\"}"
echo

echo "3) Пополнение баланса"
curl -sS -X POST "$BASE_URL/api/payment-accounts/users/$USER_ID/top-up" \
  -H "Content-Type: application/json" \
  -d '{"amount":50.00}'
echo

echo "4) Создание приложения в каталоге"
APP_ID=$(curl -sS -X POST "$BASE_URL/api/catalog/apps" \
  -H "Content-Type: application/json" \
  -d "{\"packageName\":\"com.example.demo$RUN_ID\",\"title\":\"Demo Paid App\",\"description\":\"Demo app from curl script\",\"price\":4.99}" | extract_id)
echo "APP_ID=$APP_ID"

echo "5) Обновление приложения"
curl -sS -X PUT "$BASE_URL/api/catalog/apps/$APP_ID" \
  -H "Content-Type: application/json" \
  -d "{\"packageName\":\"com.example.demo$RUN_ID\",\"title\":\"Updated Demo Paid App\",\"description\":\"Updated demo app from curl script\",\"price\":4.99}"
echo

echo "6) Просмотр каталога"
curl -sS "$BASE_URL/api/catalog/apps?query=Demo"
echo

echo "7) Добавление карты"
CARD_ID=$(curl -sS -X POST "$BASE_URL/api/cards" \
  -H "Content-Type: application/json" \
  -d "{\"userId\":$USER_ID,\"cardNumber\":\"4111111111111111\",\"holderName\":\"Student\",\"expiryMonth\":12,\"expiryYear\":2030}" | extract_id)
echo "CARD_ID=$CARD_ID"

echo "8) Обновление карты"
curl -sS -X PUT "$BASE_URL/api/cards/$CARD_ID?userId=$USER_ID" \
  -H "Content-Type: application/json" \
  -d '{"holderName":"Updated Student","expiryMonth":11,"expiryYear":2031}'
echo

echo "9) Установка платного приложения"
INSTALLATION_ID=$(curl -sS -X POST "$BASE_URL/api/installations" \
  -H "Content-Type: application/json" \
  -d "{\"userId\":$USER_ID,\"appId\":$APP_ID,\"cardId\":$CARD_ID}" | extract_id)
echo "INSTALLATION_ID=$INSTALLATION_ID"

echo "10) История покупок"
curl -sS "$BASE_URL/api/purchases?userId=$USER_ID"
echo

echo "11) Удаление установленного приложения"
curl -sS -X DELETE "$BASE_URL/api/installations/$INSTALLATION_ID?userId=$USER_ID"
echo

echo "12) Удаление карты"
curl -sS -X DELETE "$BASE_URL/api/cards/$CARD_ID?userId=$USER_ID" -i
echo

echo "13) Удаление приложения из каталога"
curl -sS -X DELETE "$BASE_URL/api/catalog/apps/$APP_ID" -i
echo

echo "14) Деактивация платежного аккаунта"
curl -sS -X DELETE "$BASE_URL/api/payment-accounts/users/$USER_ID" -i
echo
