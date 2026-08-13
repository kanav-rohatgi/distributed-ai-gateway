package com.ai.gateway_service.controller;

import com.ai.gateway_service.exception.RateLimitExceededException;
import com.ai.gateway_service.payloads.ChatRequestDTO;
import com.ai.gateway_service.payloads.ChatResponseDTO;
import com.ai.gateway_service.service.ChatService;
import com.ai.gateway_service.service.RateLimiterService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class ChatController {

    private final ChatService chatService;
    private final RateLimiterService rateLimiterService;

    public ChatController(ChatService chatService, RateLimiterService rateLimiterService) {
        this.chatService = chatService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDTO> chat(
            @RequestBody ChatRequestDTO request,
            @RequestHeader(value = "X-Client-Id", defaultValue = "anonymous") String clientId) {

        // Rate limiting
        if(!rateLimiterService.allowRequest(clientId)) {
            throw new RateLimitExceededException("Rate limit exceeded for client: " + clientId);
        }
        // handling request
        return ResponseEntity.ok(chatService.handleChat(request, clientId));
    }
}
