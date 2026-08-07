package com.ai.gateway_service.payloads;

public class ChatResponseDTO {
    private String response;
    private String providerUsed;

    public ChatResponseDTO(String response, String providerUsed) {
        this.response = response;
        this.providerUsed = providerUsed;
    }

    public String getResponse() { return response; }
    public String getProviderUsed() { return providerUsed; }
}
