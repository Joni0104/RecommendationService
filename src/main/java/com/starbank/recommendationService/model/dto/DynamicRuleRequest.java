package com.starbank.recommendationService.model.dto;

import java.util.List;
import java.util.UUID;

public record DynamicRuleRequest(
        String productName,
        UUID productId,
        String productText,
        List<RuleQuery> rule
) {}

