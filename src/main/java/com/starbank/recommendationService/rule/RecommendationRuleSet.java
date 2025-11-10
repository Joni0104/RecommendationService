package com.starbank.recommendationService.rule;

import com.starbank.recommendationService.model.RecommendationDto;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    Optional<RecommendationDto> checkRules(UUID userId);
}