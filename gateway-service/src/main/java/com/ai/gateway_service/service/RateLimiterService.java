package com.ai.gateway_service.service;

public interface RateLimiterService {
    public boolean allowRequest(String clientId);
}
