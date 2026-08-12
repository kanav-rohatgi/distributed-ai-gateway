-- KEYS[1] = tokens key, KEYS[2] = last_refill key
-- ARGV[1] = capacity, ARGV[2] = refill_rate_per_sec, ARGV[3] = now (ms)

local tokens_key = KEYS[1]
local last_refill_key = KEYS[2]

local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

local tokens = tonumber(redis.call('GET', tokens_key))
local last_refill = tonumber(redis.call('GET', last_refill_key))

if tokens == nil then
    tokens = capacity
    last_refill = now
end

local elapsed_seconds = (now - last_refill) / 1000.0
local refill_amount = elapsed_seconds * refill_rate
tokens = math.min(capacity, tokens + refill_amount)

local allowed = 0
if tokens >= 1 then
    tokens = tokens - 1
    allowed = 1
end

redis.call('SET', tokens_key, tostring(tokens))
redis.call('SET', last_refill_key, tostring(now))

return allowed