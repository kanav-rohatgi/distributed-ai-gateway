#!/usr/bin/env bash
set -e
BASE_URL="http://localhost"

echo "=== 1. Generating API key ==="
RESPONSE=$(curl -s -X POST "$BASE_URL/admin/keys" \
  -H "Content-Type: application/json" \
  -d '{"clientName": "demo-client", "rateLimitCapacity": 5}')
echo "$RESPONSE"
API_KEY=$(echo "$RESPONSE" | grep -o '"apiKey":"[^"]*"' | cut -d'"' -f4)
[ -z "$API_KEY" ] && echo "Failed to get API key" && exit 1
echo "Key: $API_KEY"

echo ""
echo "=== 2. Normal request ==="
curl -s -X POST "$BASE_URL/v1/chat" -H "Content-Type: application/json" \
  -H "Authorization: Bearer $API_KEY" -d '{"prompt": "say hello in 5 words"}'
echo ""

echo ""
echo "=== 3. No API key (should 401) ==="
curl -s -X POST "$BASE_URL/v1/chat" -H "Content-Type: application/json" \
  -d '{"prompt": "hi"}'
echo ""

echo ""
echo "=== 4. Hammering past rate limit (capacity 5) ==="
for i in {1..7}; do
  echo "--- Request $i ---"
  curl -s -X POST "$BASE_URL/v1/chat" -H "Content-Type: application/json" \
    -H "Authorization: Bearer $API_KEY" -d '{"prompt": "hi"}'
  echo ""
done
echo "Expected: first ~5 succeed, rest return 429"

echo ""
echo "=== 5. Provider fallback ==="
echo "Stop Ollama, then re-run a chat request — check logs for providerUsed: groq"

echo ""
echo "=== 6. Circuit breaker ==="
echo "With Ollama still down, fire several more requests — logs shift from"
echo "real connection-refused errors to instant CallNotPermittedException"

echo ""
echo "Demo complete."
