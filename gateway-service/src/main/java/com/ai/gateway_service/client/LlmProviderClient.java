package com.ai.gateway_service.client;

public interface LlmProviderClient {

    String sendPrompt(String prompt);

    String getProviderName();
}
