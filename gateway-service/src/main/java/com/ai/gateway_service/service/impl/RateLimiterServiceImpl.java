package com.ai.gateway_service.service.impl;

import com.ai.gateway_service.model.TokenBucket;
import com.ai.gateway_service.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private final ConcurrentMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    @Value("${ratelimit.capacity}")
    private double capacity;

    @Value("${ratelimit.refill-rate-per-second}")
    private double refillRatePerSecond;

    @Override
    public boolean allowRequest(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(
                clientId,
                id -> new TokenBucket(capacity, refillRatePerSecond)
        );
        return bucket.tryConsume();
    }


}
