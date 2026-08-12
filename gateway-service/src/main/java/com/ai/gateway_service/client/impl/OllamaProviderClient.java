package com.ai.gateway_service.client.impl;

import com.ai.gateway_service.client.LlmProviderClient;
import com.ai.gateway_service.payloads.OllamaChatResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class OllamaProviderClient implements LlmProviderClient {
    private final RestTemplate restTemplate;

    @Value("${llm.provider.ollama.base-url}")
    private String baseUrl;

    @Value("${llm.provider.ollama.model}")
    private String model;

    public OllamaProviderClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @CircuitBreaker(name = "ollama")
    public String sendPrompt(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Building the json to send to ollama
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        // bundles the body and header
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<OllamaChatResponse> response =
                restTemplate.postForEntity(baseUrl, entity, OllamaChatResponse.class);

        return response.getBody()
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();

    }

    @Override
    public String getProviderName() {
        return "ollama";
    }

}
