cat > demo.sh << 'EOF'
#!/usr/bin/env bash
set -e
BASE_URL="http://localhost"
COST_TRACKING_URL="http://localhost:8082"

echo "=== 1. Generating API key ==="
RESPONSE=$(curl -s -X POST "$BASE_URL/admin/keys" \
  -H "Content-Type: application/json" \
  -d '{"clientName": "demo-client", "rateLimitCapacity": 5}')
echo "$RESPONSE"
API_KEY=$(echo "$RESPONSE" | grep -o '"apiKey":"[^"]*"' | cut -d'"' -f4)
[ -z "$API_KEY" ] && echo "Failed to get API key" && exit 1
echo "Key: $API_KEY"

echo ""
echo "=== 2. Cost Tracking Service — direct health check ==="
curl -s -X POST "$COST_TRACKING_URL/internal/usage" \
  -H "Content-Type: application/json" \
  -d '{"clientId": "demo-client", "provider": "test", "tokensEstimate": 1, "status": "success"}'
echo " (expect empty 200 OK — if this errors, cost-tracking-service is down)"

echo ""
echo "=== 3. Normal chat request ==="
curl -s -X POST "$BASE_URL/v1/chat" -H "Content-Type: application/json" \
  -H "Authorization: Bearer $API_KEY" -d '{"prompt": "say hello in 5 words"}'
echo ""

echo ""
echo "=== 4. No API key (should 401) ==="
curl -s -X POST "$BASE_URL/v1/chat" -H "Content-Type: application/json" \
  -d '{"prompt": "hi"}'
echo ""

echo ""
echo "=== 5. Hammering past rate limit (capacity 5) ==="
for i in {1..7}; do
  echo "--- Request $i ---"
  curl -s -X POST "$BASE_URL/v1/chat" -H "Content-Type: application/json" \
    -H "Authorization: Bearer $API_KEY" -d '{"prompt": "hi"}'
  echo ""
done
echo "Expected: first ~5 succeed, rest return 429"

echo ""
echo "=== 6. Verify usage was actually logged ==="
sleep 2
curl -s "$COST_TRACKING_URL/usage/demo-client"
echo ""
echo "(expect totalRequests to roughly match successful chats above)"

echo ""
echo "=== 7. Provider fallback ==="
echo "Ollama can't be reliably killed on macOS (its menu bar app respawns the"
echo "process). Instead, simulate it being down:"
echo ""
echo "  1. Edit config-repo/gateway-service.yml"
echo "  2. Change llm.provider.ollama.base-url to http://host.docker.internal:19999/v1/chat/completions"
echo "  3. docker-compose restart gateway-1 gateway-2 gateway-3"
echo "  4. Re-run a chat request — check logs for providerUsed: groq"
echo "  5. Revert the port back to 11434 when done testing"

echo ""
echo "=== 8. Circuit breaker ==="
echo "With Ollama still down, fire several more requests — logs shift from"
echo "real connection-refused errors to instant CallNotPermittedException"

echo ""
echo "=== 9. Inspect Postgres directly ==="
echo "  docker exec -it distributed-ai-gateway-postgres-1 psql -U postgres -d cost_tracking"
echo "  Inside psql:"
echo "    \\dt                       -- list tables"
echo "    SELECT * FROM usage_log;   -- see logged requests"
echo "    SELECT * FROM api_keys;    -- see generated keys (hashed)"
echo "    \\q                        -- exit"

echo ""
echo "Demo complete."
EOF

chmod +x demo.sh