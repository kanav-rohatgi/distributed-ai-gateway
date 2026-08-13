package com.ai.gateway_service.client;

public interface CostTrackingClient {
    void logUsage(String clientId, String provider, String status);;
}
