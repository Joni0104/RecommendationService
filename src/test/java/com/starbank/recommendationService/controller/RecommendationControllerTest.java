package com.starbank.recommendationService.controller;

import com.starbank.recommendationService.model.RecommendationDto;
import com.starbank.recommendationService.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    void getRecommendations_WhenValidUserId_ShouldReturnRecommendations() throws Exception {
        // Arrange
        UUID userId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");
        RecommendationDto recommendation = new RecommendationDto(
                "Invest 500",
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                "Test description"
        );

        when(recommendationService.getRecommendations(any(UUID.class)))
                .thenReturn(List.of(recommendation));

        // Act & Assert
        mockMvc.perform(get("/recommendation/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userId.toString()))
                .andExpect(jsonPath("$.recommendations[0].name").value("Invest 500"));
    }

    @Test
    void getRecommendations_WhenNoRecommendations_ShouldReturnEmptyList() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(recommendationService.getRecommendations(any(UUID.class)))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/recommendation/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendations").isEmpty());
    }
}