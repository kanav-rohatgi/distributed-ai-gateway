package com.ai.cost_tracking_service.service.impl;

import com.ai.cost_tracking_service.model.UsageLog;
import com.ai.cost_tracking_service.payload.UsageLogRequestDTO;
import com.ai.cost_tracking_service.payload.UsageSummaryDTO;
import com.ai.cost_tracking_service.repository.UsageLogRepository;
import com.ai.cost_tracking_service.service.UsageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsageServiceImpl implements UsageService {

    private final UsageLogRepository usageLogRepository;

    public UsageServiceImpl(UsageLogRepository usageLogRepository) {
        this.usageLogRepository = usageLogRepository;
    }


    @Override
    public void logUsage(UsageLogRequestDTO request) {
        UsageLog log = new UsageLog(
                request.getClientId(),
                request.getProvider(),
                request.getTokensEstimate(),
                request.getStatus()
        );
        usageLogRepository.save(log);
    }

    @Override
    public UsageSummaryDTO getUsageSummary(String clientId) {
        List<UsageLog> logs = usageLogRepository.findByClientId(clientId);
        List<String> providers = logs.stream()
                .map(UsageLog::getProvider)
                .distinct()
                .collect(Collectors.toList());
        return new UsageSummaryDTO(clientId, logs.size(), providers);
    }


}
