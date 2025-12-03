package com.starbank.recommendationService.model.dto;

import java.util.List;
import java.util.UUID;

public record RuleStatisticResponse(
        List<RuleStatisticDto> stats
) {}

