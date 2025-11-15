package com.starbank.recommendationService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbank.recommendationService.model.dto.DynamicRuleRequest;
import com.starbank.recommendationService.model.dto.DynamicRuleResponse;

import com.starbank.recommendationService.model.dto.RuleQuery;
import com.starbank.recommendationService.model.entity.DynamicRuleEntity;
import com.starbank.recommendationService.repository.DynamicRuleRepository;
import com.starbank.recommendationService.repository.UserDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DynamicRuleService {

    private final DynamicRuleRepository dynamicRuleRepository;
    private final UserDataRepository userDataRepository;
    private final ObjectMapper objectMapper;

    public DynamicRuleService(DynamicRuleRepository dynamicRuleRepository,
                              UserDataRepository userDataRepository,
                              ObjectMapper objectMapper) {
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.userDataRepository = userDataRepository;
        this.objectMapper = objectMapper;
    }

    public DynamicRuleResponse createRule(DynamicRuleRequest request) {
        try {
            String ruleJson = objectMapper.writeValueAsString(request.rule());

            DynamicRuleEntity entity = new DynamicRuleEntity(
                    request.productName(),
                    request.productId(),
                    request.productText(),
                    ruleJson
            );

            DynamicRuleEntity savedEntity = dynamicRuleRepository.save(entity);

            return new DynamicRuleResponse(
                    savedEntity.getId(),
                    savedEntity.getProductName(),
                    savedEntity.getProductId(),
                    savedEntity.getProductText(),
                    request.rule()
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing rule JSON", e);
        }
    }

    public List<DynamicRuleResponse> getAllRules() {
        return dynamicRuleRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    public void deleteRule(UUID productId) {
        dynamicRuleRepository.deleteByProductId(productId);
    }

    public boolean checkDynamicRule(UUID userId, DynamicRuleEntity rule) {
        try {
            List<RuleQuery> ruleQueries = objectMapper.readValue(
                    rule.getRule(),
                    new TypeReference<List<RuleQuery>>() {}
            );

            for (RuleQuery query : ruleQueries) {
                boolean result = evaluateQuery(userId, query);
                if (!result) {
                    return false;
                }
            }
            return true;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing rule JSON", e);
        }
    }

    private boolean evaluateQuery(UUID userId, RuleQuery query) {
        boolean result = switch (query.query()) {
            case "USER_OF" -> evaluateUserOf(userId, query);
            case "ACTIVE_USER_OF" -> evaluateActiveUserOf(userId, query);
            case "TRANSACTION_SUM_COMPARE" -> evaluateTransactionSumCompare(userId, query);
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> evaluateDepositWithdrawCompare(userId, query);
            default -> throw new IllegalArgumentException("Unknown query type: " + query.query());
        };

        return query.negate() != result;
    }

    private boolean evaluateUserOf(UUID userId, RuleQuery query) {
        String productType = query.arguments().get(0);
        return userDataRepository.hasProductType(userId, productType);
    }

    private boolean evaluateActiveUserOf(UUID userId, RuleQuery query) {
        String productType = query.arguments().get(0);
        int transactionCount = userDataRepository.getTransactionCountByProductType(userId, productType);
        return transactionCount >= 5;
    }

    private boolean evaluateTransactionSumCompare(UUID userId, RuleQuery query) {
        String productType = query.arguments().get(0);
        String transactionType = query.arguments().get(1);
        String operator = query.arguments().get(2);
        double value = Double.parseDouble(query.arguments().get(3));

        double sum = transactionType.equals("DEPOSIT")
                ? userDataRepository.getTotalDepositsByProductType(userId, productType)
                : userDataRepository.getTotalSpendsByProductType(userId, productType);

        return compareValues(sum, operator, value);
    }

    private boolean evaluateDepositWithdrawCompare(UUID userId, RuleQuery query) {
        String productType = query.arguments().get(0);
        String operator = query.arguments().get(1);

        double deposits = userDataRepository.getTotalDepositsByProductType(userId, productType);
        double spends = userDataRepository.getTotalSpendsByProductType(userId, productType);

        return compareValues(deposits, operator, spends);
    }

    private boolean compareValues(double left, String operator, double right) {
        return switch (operator) {
            case ">" -> left > right;
            case "<" -> left < right;
            case "=" -> Math.abs(left - right) < 0.001;
            case ">=" -> left >= right;
            case "<=" -> left <= right;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    private DynamicRuleResponse convertToResponse(DynamicRuleEntity entity) {
        try {
            List<RuleQuery> rule = objectMapper.readValue(
                    entity.getRule(),
                    new TypeReference<List<RuleQuery>>() {}
            );

            return new DynamicRuleResponse(
                    entity.getId(),
                    entity.getProductName(),
                    entity.getProductId(),
                    entity.getProductText(),
                    rule
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting entity to response", e);
        }
    }
}