#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "1) Создание платежного аккаунта"
USER_ID=$(curl -sS -X POST "$BASE_URL/api/payment-accounts/users" \
  -H "Content-Type: application/json" \
  -d '{"email":"student@example.com","displayName":"Student","initialBalance":100.00}' | sed -n 's/.*"id":\([0-9]*\).*/\1/p')

echo "USER_ID=$USER_ID"

echo "2) Пополнение баланса"
curl -sS -X POST "$BASE_URL/api/payment-accounts/users/$USER_ID/top-up" \
  -H "Content-Type: application/json" \
  -d '{"amount":50.00}'
echo

echo "3) Просмотр каталога"
curl -sS "$BASE_URL/api/catalog/apps?query="
echo

echo "4) Добавление карты"
CARD_ID=$(curl -sS -X POST "$BASE_URL/api/cards" \
  -H "Content-Type: application/json" \
  -d "{\"userId\":$USER_ID,\"cardNumber\":\"4111111111111111\",\"holderName\":\"Student\",\"expiryMonth\":12,\"expiryYear\":2030}" | sed -n 's/.*"id":\([0-9]*\).*/\1/p')

echo "CARD_ID=$CARD_ID"

echo "5) Установка платного приложения (appId=2)"
curl -sS -X POST "$BASE_URL/api/installations" \
  -H "Content-Type: application/json" \
  -d "{\"userId\":$USER_ID,\"appId\":2,\"cardId\":$CARD_ID}"
echo

echo "6) История покупок"
curl -sS "$BASE_URL/api/purchases?userId=$USER_ID"
echo

echo "7) История установок"
curl -sS "$BASE_URL/api/installations?userId=$USER_ID"
echo

echo "8) Удаление карты"
curl -sS -X DELETE "$BASE_URL/api/cards/$CARD_ID?userId=$USER_ID" -i
echo
