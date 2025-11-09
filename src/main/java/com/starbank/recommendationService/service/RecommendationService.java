package com.starbank.recommendationService.service;

import com.starbank.recommendationService.model.RecommendationDto;
import com.starbank.recommendationService.rule.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;

    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public List<RecommendationDto> getRecommendations(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        return ruleSets.stream()
                .map(ruleSet -> ruleSet.checkRules(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}