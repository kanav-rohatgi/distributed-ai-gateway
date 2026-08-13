package com.ai.gateway_service.service.impl;

import com.ai.gateway_service.client.CostTrackingClient;
import com.ai.gateway_service.client.LlmProviderClient;
import com.ai.gateway_service.payloads.ChatRequestDTO;
import com.ai.gateway_service.payloads.ChatResponseDTO;
import com.ai.gateway_service.service.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    private final List<LlmProviderClient> providerClient;
    private final CostTrackingClient costTrackingClient;

    @Value("${llm.provider.primary}")
    private String primaryProviderName;

    public ChatServiceImpl(List<LlmProviderClient> providerClients, CostTrackingClient costTrackingClient) {
        this.providerClient = providerClients;
        this.costTrackingClient = costTrackingClient;
    }

    @Override
    public ChatResponseDTO handleChat(ChatRequestDTO request) {
        return handleChat(request, "anonymous");
    }

    @Override
    public ChatResponseDTO handleChat(ChatRequestDTO request, String clientId) {
        // Order providers so the configured primary is tried first
        List<LlmProviderClient> orderedClients = providerClient.stream()
                .sorted(Comparator.comparing(c -> !c.getProviderName().equals(primaryProviderName))).toList();

        RuntimeException lastFailure = null;

        for (LlmProviderClient client : orderedClients) {
            try {
                String result = client.sendPrompt(request.getPrompt());
                logUsageAsync(clientId, client.getProviderName(), "success");
                return new ChatResponseDTO(result, client.getProviderName());
            } catch (Exception e) {
                System.err.println("Provider [" + client.getProviderName() + "] failed: " + e.getMessage());
                lastFailure = new RuntimeException(e);
            }
        }
        logUsageAsync(clientId, "none", "failed");
        throw new RuntimeException("All providers failed", lastFailure);
    }

    @Async
    protected void logUsageAsync(String clientId, String provider, String status) {
        try {
            costTrackingClient.logUsage(clientId, provider, status);
        } catch (Exception e) {
            System.err.println("Failed to log usage: " + e.getMessage());
        }
    }
}
