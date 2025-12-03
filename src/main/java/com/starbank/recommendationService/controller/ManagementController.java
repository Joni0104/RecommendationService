package com.starbank.recommendationService.controller;

import com.starbank.recommendationService.model.dto.RuleStatisticResponse;
import com.starbank.recommendationService.service.DynamicRuleService;
import com.starbank.recommendationService.service.cache.CacheService;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private final CacheService cacheService;
    private final DynamicRuleService dynamicRuleService;
    private final BuildProperties buildProperties;

    public ManagementController(CacheService cacheService,
                                DynamicRuleService dynamicRuleService,
                                BuildProperties buildProperties) {
        this.cacheService = cacheService;
        this.dynamicRuleService = dynamicRuleService;
        this.buildProperties = buildProperties;
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        cacheService.clearAllCaches();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getServiceInfo() {
        Map<String, String> info = Map.of(
                "name", "Recommendation Service",
                "version", buildProperties.getVersion()
        );
        return ResponseEntity.ok(info);
    }

    @GetMapping("/rule/stats")
    public ResponseEntity<RuleStatisticResponse> getRuleStatistics() {
        RuleStatisticResponse stats = dynamicRuleService.getRuleStatistics();
        return ResponseEntity.ok(stats);
    }
}
