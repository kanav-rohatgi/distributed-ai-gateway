package com.ai.gateway_service.payloads;

public class GenerateKeyRequestDTO {
    private String clientName;
    private Double rateLimitCapacity;

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public Double getRateLimitCapacity() { return rateLimitCapacity; }
    public void setRateLimitCapacity(Double rateLimitCapacity) { this.rateLimitCapacity = rateLimitCapacity; }
}
