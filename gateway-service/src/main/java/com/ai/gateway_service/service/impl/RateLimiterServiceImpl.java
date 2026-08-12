package com.ai.gateway_service.service.impl;

import com.ai.gateway_service.repository.RateLimiterRedisRepository;
import com.ai.gateway_service.service.RateLimiterService;
import org.springframework.stereotype.Service;


@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private final RateLimiterRedisRepository rateLimiterRedisRepository;

    public RateLimiterServiceImpl(RateLimiterRedisRepository rateLimiterRedisRepository) {
        this.rateLimiterRedisRepository = rateLimiterRedisRepository;
    }

    @Override
    public boolean allowRequest(String clientId) {
        return rateLimiterRedisRepository.tryConsume(clientId);
    }


}
