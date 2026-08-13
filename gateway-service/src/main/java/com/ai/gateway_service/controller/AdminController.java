package com.ai.gateway_service.controller;

import com.ai.gateway_service.model.ApiKey;
import com.ai.gateway_service.payloads.GenerateKeyRequestDTO;
import com.ai.gateway_service.repository.ApiKeyRepository;
import com.ai.gateway_service.util.ApiKeyHasher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyHasher apiKeyHasher;

    public AdminController(ApiKeyRepository apiKeyRepository, ApiKeyHasher apiKeyHasher) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyHasher = apiKeyHasher;
    }

    @PostMapping("/keys")
    public ResponseEntity<Map<String, String>> generateKey(@RequestBody GenerateKeyRequestDTO request) {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        String rawKey = "sk-" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        String hashed = apiKeyHasher.hash(rawKey);

        ApiKey apiKey = new ApiKey(
                request.getClientName(),
                hashed,
                request.getRateLimitCapacity() != null ? request.getRateLimitCapacity() : 5.0
        );
        apiKeyRepository.save(apiKey);

        // Raw key is returned ONLY here once, it's never retrievable again
        return ResponseEntity.ok(Map.of("apiKey", rawKey, "clientName", request.getClientName()));
    }
}
