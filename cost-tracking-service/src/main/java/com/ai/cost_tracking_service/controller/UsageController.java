package com.ai.cost_tracking_service.controller;

import com.ai.cost_tracking_service.payload.UsageLogRequestDTO;
import com.ai.cost_tracking_service.payload.UsageSummaryDTO;
import com.ai.cost_tracking_service.service.UsageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UsageController {

    private final UsageService usageService;

    public UsageController(UsageService usageService) {
        this.usageService = usageService;
    }

    @PostMapping("/internal/usage")
    public ResponseEntity<Void> logUsage(@RequestBody UsageLogRequestDTO request) {
        usageService.logUsage(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/usage/{clientId}")
    public ResponseEntity<UsageSummaryDTO> getUsage(@PathVariable String clientId) {
        return ResponseEntity.ok(usageService.getUsageSummary(clientId));
    }
}
