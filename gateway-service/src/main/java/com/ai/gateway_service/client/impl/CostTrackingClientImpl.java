package com.ai.gateway_service.client.impl;

import com.ai.gateway_service.client.CostTrackingClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class CostTrackingClientImpl implements CostTrackingClient {

    private final RestTemplate loadBalancedRestTemplate;

    public CostTrackingClientImpl(@Qualifier("loadBalancedRestTemplate") RestTemplate loadBalancedRestTemplate) {
        this.loadBalancedRestTemplate = loadBalancedRestTemplate;
    }

    @Override
    public void logUsage(String clientId, String provider, String status) {
        // building the json to send as Request Body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "clientId", clientId,
                "provider", provider,
                "tokensEstimate", 0,
                "status", status
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        loadBalancedRestTemplate.postForEntity(
                "http://cost-tracking-service/internal/usage",
                entity,
                Void.class
        );
    }
}
