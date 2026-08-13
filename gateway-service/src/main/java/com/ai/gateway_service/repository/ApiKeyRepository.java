package com.ai.gateway_service.repository;

import com.ai.gateway_service.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    Optional<ApiKey> findByHashedKeyAndActiveTrue(String hashedKey);
}
