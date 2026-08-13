package com.ai.cost_tracking_service.payload;

import java.util.List;

public class UsageSummaryDTO {
    private String clientId;
    private long totalRequests;
    private List<String> providersUsed;

    public UsageSummaryDTO(String clientId, long totalRequests, List<String> providersUsed) {
        this.clientId = clientId;
        this.totalRequests = totalRequests;
        this.providersUsed = providersUsed;
    }

    public String getClientId() { return clientId; }
    public long getTotalRequests() { return totalRequests; }
    public List<String> getProvidersUsed() { return providersUsed; }
}
