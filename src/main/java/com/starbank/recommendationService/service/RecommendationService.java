package com.starbank.recommendationService.service;

import com.starbank.recommendationService.model.RecommendationDto;
import com.starbank.recommendationService.model.RecommendationResponse;
import com.starbank.recommendationService.model.entity.DynamicRuleEntity;
import com.starbank.recommendationService.repository.DynamicRuleRepository;
import com.starbank.recommendationService.rule.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> staticRuleSets;
    private final DynamicRuleRepository dynamicRuleRepository;
    private final DynamicRuleService dynamicRuleService;

    public RecommendationService(List<RecommendationRuleSet> staticRuleSets,
                                 DynamicRuleRepository dynamicRuleRepository,
                                 DynamicRuleService dynamicRuleService) {
        this.staticRuleSets = staticRuleSets;
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.dynamicRuleService = dynamicRuleService;
    }

    public RecommendationResponse getRecommendations(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        List<RecommendationDto> recommendations = new ArrayList<>();

        // Статические правила
        recommendations.addAll(getStaticRecommendations(userId));

        // Динамические правила
        recommendations.addAll(getDynamicRecommendations(userId));

        return new RecommendationResponse(userId, recommendations);
    }

    private List<RecommendationDto> getStaticRecommendations(UUID userId) {
        if (staticRuleSets == null) {
            return List.of();
        }

        return staticRuleSets.stream()
                .map(ruleSet -> ruleSet.checkRules(userId))
                .filter(optional -> optional.isPresent())
                .map(optional -> optional.get())
                .toList();
    }

    private List<RecommendationDto> getDynamicRecommendations(UUID userId) {
        List<DynamicRuleEntity> dynamicRules = dynamicRuleRepository.findAll();

        return dynamicRules.stream()
                .filter(rule -> dynamicRuleService.checkDynamicRule(userId, rule))
                .map(rule -> new RecommendationDto(
                        rule.getProductName(),
                        rule.getProductId(),
                        rule.getProductText()
                ))
                .toList();
    }
}