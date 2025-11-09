package com.starbank.recommendationService.controller;

import com.starbank.recommendationService.model.RecommendationResponse;
import com.starbank.recommendationService.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @PathVariable UUID userId) {

        try {
            var recommendations = recommendationService.getRecommendations(userId);
            var response = new RecommendationResponse(userId, recommendations);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}