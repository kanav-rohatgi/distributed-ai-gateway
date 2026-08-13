package com.ai.gateway_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
public class ApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "hashed_key", nullable = false, unique = true)
    private String hashedKey;

    @Column(name = "rate_limit_capacity", nullable = false)
    private Double rateLimitCapacity = 5.0;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ApiKey() {}

    public ApiKey(String clientName, String hashedKey, Double rateLimitCapacity) {
        this.clientName = clientName;
        this.hashedKey = hashedKey;
        this.rateLimitCapacity = rateLimitCapacity;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getHashedKey() {
        return hashedKey;
    }

    public void setHashedKey(String hashedKey) {
        this.hashedKey = hashedKey;
    }

    public Double getRateLimitCapacity() {
        return rateLimitCapacity;
    }

    public void setRateLimitCapacity(Double rateLimitCapacity) {
        this.rateLimitCapacity = rateLimitCapacity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
