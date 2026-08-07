package com.ai.gateway_service.service.impl;

import com.ai.gateway_service.client.LlmProviderClient;
import com.ai.gateway_service.payloads.ChatRequestDTO;
import com.ai.gateway_service.payloads.ChatResponseDTO;
import com.ai.gateway_service.service.ChatService;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {
    private final LlmProviderClient providerClient;

    public ChatServiceImpl(LlmProviderClient providerClient) {
        this.providerClient = providerClient;
    }

    @Override
    public ChatResponseDTO handleChat(ChatRequestDTO request) {
        String result = providerClient.sendPrompt(request.getPrompt());
        return new ChatResponseDTO(result, providerClient.getProviderName());
    }
}
