package com.starbank.recommendationService.controller;

import com.starbank.recommendationService.model.dto.DynamicRuleRequest;
import com.starbank.recommendationService.model.dto.DynamicRuleResponse;
import com.starbank.recommendationService.model.dto.RulesListResponse;
import com.starbank.recommendationService.service.DynamicRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/rule")
public class DynamicRuleController {

    private final DynamicRuleService dynamicRuleService;

    public DynamicRuleController(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }

    @PostMapping
    public ResponseEntity<DynamicRuleResponse> createRule(@RequestBody DynamicRuleRequest request) {
        DynamicRuleResponse response = dynamicRuleService.createRule(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<RulesListResponse> getAllRules() {
        var rules = dynamicRuleService.getAllRules();
        return ResponseEntity.ok(new RulesListResponse(rules));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteRule(@PathVariable UUID productId) {
        dynamicRuleService.deleteRule(productId);
        return ResponseEntity.noContent().build();
    }
}