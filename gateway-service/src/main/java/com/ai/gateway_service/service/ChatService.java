package com.ai.gateway_service.service;

import com.ai.gateway_service.payloads.ChatRequestDTO;
import com.ai.gateway_service.payloads.ChatResponseDTO;

public interface ChatService {
    ChatResponseDTO handleChat(ChatRequestDTO request, String clientId);

    ChatResponseDTO handleChat(ChatRequestDTO request);
}
