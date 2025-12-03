package com.starbank.recommendationService.controller;

import com.starbank.recommendationService.model.RecommendationDto;
import com.starbank.recommendationService.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<RecommendationDto>> getRecommendations(
            @PathVariable UUID userId) {

        try {
            List<RecommendationDto> response = recommendationService.getRecommendations(userId).recommendations();
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}