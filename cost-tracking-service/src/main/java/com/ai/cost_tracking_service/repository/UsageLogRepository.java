package com.ai.cost_tracking_service.repository;

import com.ai.cost_tracking_service.model.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UsageLogRepository extends JpaRepository<UsageLog, UUID> {
    List<UsageLog> findByClientId(String clientId);
}
