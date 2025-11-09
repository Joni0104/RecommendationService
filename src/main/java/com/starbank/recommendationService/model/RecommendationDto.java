package com.starbank.recommendationService.model;

import java.util.UUID;

public record RecommendationDto(
        String name,
        UUID id,
        String text
) {
    public RecommendationDto {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Product description cannot be null or empty");
        }
    }
}