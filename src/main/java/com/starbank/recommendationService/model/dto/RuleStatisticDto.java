package com.starbank.recommendationService.model.dto;

import java.util.UUID;

public record RuleStatisticDto(
        UUID ruleId,
        Long count
) {}
