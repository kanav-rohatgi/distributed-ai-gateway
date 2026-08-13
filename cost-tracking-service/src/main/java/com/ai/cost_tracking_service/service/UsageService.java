package com.ai.cost_tracking_service.service;

import com.ai.cost_tracking_service.payload.UsageLogRequestDTO;
import com.ai.cost_tracking_service.payload.UsageSummaryDTO;

public interface UsageService {
    void logUsage(UsageLogRequestDTO request);

    UsageSummaryDTO getUsageSummary(String clientId);
}
