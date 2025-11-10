package com.starbank.recommendationService.model;



import java.util.List;
import java.util.UUID;

public record RecommendationResponse(
        UUID userId,
        List<RecommendationDto> recommendations
) {
    public RecommendationResponse {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (recommendations == null) {
            recommendations = List.of();
        }
    }
}