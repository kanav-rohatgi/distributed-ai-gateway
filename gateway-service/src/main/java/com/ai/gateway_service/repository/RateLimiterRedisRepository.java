package com.ai.gateway_service.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RateLimiterRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final DefaultRedisScript<Long> tokenBucketScript;

    @Value("${ratelimit.capacity}")
    private double capacity;

    @Value("${ratelimit.refill-rate-per-second}")
    private double refillRatePerSecond;

    public RateLimiterRedisRepository(RedisTemplate<String, String> redisTemplate,
                                      DefaultRedisScript<Long> tokenBucketScript) {
        this.redisTemplate = redisTemplate;
        this.tokenBucketScript = tokenBucketScript;
    }

    public boolean tryConsume(String clientId) {
        String tokensKey = "ratelimit:" + clientId + ":tokens";
        String lastRefillKey = "ratelimit:" + clientId + ":lastRefill";

        Long result = redisTemplate.execute(
                tokenBucketScript,
                List.of(tokensKey, lastRefillKey),
                String.valueOf(capacity),
                String.valueOf(refillRatePerSecond),
                String.valueOf(System.currentTimeMillis())
        );

        return result != null && result == 1L; // if both true, return true;
    }
}
