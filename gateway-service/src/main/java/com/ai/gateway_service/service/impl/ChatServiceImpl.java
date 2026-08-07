package com.ai.gateway_service.service.impl;

import com.ai.gateway_service.client.LlmProviderClient;
import com.ai.gateway_service.payloads.ChatRequestDTO;
import com.ai.gateway_service.payloads.ChatResponseDTO;
import com.ai.gateway_service.service.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ChatServiceImpl implements ChatService {
    private final List<LlmProviderClient> providerClient;

    @Value("${llm.provider.primary}")
    private String primaryProviderName;

    public ChatServiceImpl(List<LlmProviderClient> providerClient) {
        this.providerClient = providerClient;
    }

    @Override
    public ChatResponseDTO handleChat(ChatRequestDTO request) {
        // Order providers so the configured primary is tried first
        List<LlmProviderClient> orderedClients = providerClient.stream()
                .sorted(Comparator.comparing(c -> !c.getProviderName().equals(primaryProviderName))).toList();

        RuntimeException lastFailure = null;

        for (LlmProviderClient client : orderedClients) {
            try {
                String result = client.sendPrompt(request.getPrompt());
                return new ChatResponseDTO(result, client.getProviderName());
            } catch (Exception e) {
                System.err.println("Provider [" + client.getProviderName() + "] failed: " + e.getMessage());
                lastFailure = new RuntimeException(e);
            }
        }
        throw new RuntimeException("All providers failed", lastFailure);
    }
}
