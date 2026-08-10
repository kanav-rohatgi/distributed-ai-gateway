package com.ai.gateway_service.model;

public class TokenBucket {
    private double tokens;
    private final double capacity;
    private final double refillRatePerSecond;
    private long lastRefillTimestamp;

    public TokenBucket(double capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.tokens = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean tryConsume() {
        refill();
        if(tokens >= 1) {
            tokens -= 1;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double secondsElapsed = (now - lastRefillTimestamp) / 1000.0;
        double tokensToAdd = secondsElapsed * refillRatePerSecond;
        tokens = Math.min(capacity, tokens + tokensToAdd);
        lastRefillTimestamp = now;
    }
}
