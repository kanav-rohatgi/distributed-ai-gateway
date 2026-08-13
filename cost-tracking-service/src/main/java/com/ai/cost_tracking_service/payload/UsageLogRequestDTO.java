package com.ai.cost_tracking_service.payload;

public class UsageLogRequestDTO {
    private String clientId;
    private String provider;
    private Integer tokensEstimate;
    private String status;

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public Integer getTokensEstimate() { return tokensEstimate; }
    public void setTokensEstimate(Integer tokensEstimate) { this.tokensEstimate = tokensEstimate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
