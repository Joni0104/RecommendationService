package com.starbank.recommendationService.model.dto;

import java.util.List;

public record RulesListResponse(
        List<DynamicRuleResponse> data
) {}