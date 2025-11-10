package com.starbank.recommendationService.model.dto;

import java.util.List;

public record RuleQuery(
        String query,
        List<String> arguments,
        boolean negate
) {}
