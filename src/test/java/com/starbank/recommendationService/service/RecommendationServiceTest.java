package com.starbank.recommendationService.service;

import com.starbank.recommendationService.model.RecommendationDto;
import com.starbank.recommendationService.rule.RecommendationRuleSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRuleSet ruleSet1;

    @Mock
    private RecommendationRuleSet ruleSet2;

    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(List.of(ruleSet1, ruleSet2));
    }

    @Test
    void getRecommendations_WhenRulesReturnRecommendations_ShouldReturnList() {
        UUID userId = UUID.randomUUID();
        RecommendationDto recommendation = new RecommendationDto("Test", UUID.randomUUID(), "Description");

        when(ruleSet1.checkRules(userId)).thenReturn(Optional.of(recommendation));
        when(ruleSet2.checkRules(userId)).thenReturn(Optional.empty());

        List<RecommendationDto> result = recommendationService.getRecommendations(userId);

        assertEquals(1, result.size());
        assertEquals(recommendation, result.get(0));
    }

    @Test
    void getRecommendations_WhenNullUserId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> recommendationService.getRecommendations(null));
    }
}