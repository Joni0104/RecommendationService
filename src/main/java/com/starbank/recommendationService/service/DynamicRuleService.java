package com.starbank.recommendationService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbank.recommendationService.model.dto.DynamicRuleRequest;
import com.starbank.recommendationService.model.dto.DynamicRuleResponse;
import com.starbank.recommendationService.model.dto.RuleQuery;
import com.starbank.recommendationService.model.dto.RuleStatisticResponse;
import com.starbank.recommendationService.model.dto.RuleStatisticDto;
import com.starbank.recommendationService.model.entity.DynamicRuleEntity;
import com.starbank.recommendationService.model.entity.RuleStatisticEntity;
import com.starbank.recommendationService.repository.DynamicRuleRepository;
import com.starbank.recommendationService.repository.RuleStatisticRepository;
import com.starbank.recommendationService.repository.UserDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class DynamicRuleService {

    private final DynamicRuleRepository dynamicRuleRepository;
    private final RuleStatisticRepository ruleStatisticRepository;
    private final UserDataRepository userDataRepository;
    private final ObjectMapper objectMapper;

    public DynamicRuleService(DynamicRuleRepository dynamicRuleRepository,
                              RuleStatisticRepository ruleStatisticRepository,
                              UserDataRepository userDataRepository,
                              ObjectMapper objectMapper) {
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.ruleStatisticRepository = ruleStatisticRepository;
        this.userDataRepository = userDataRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public DynamicRuleResponse createRule(DynamicRuleRequest request) {
        try {
            // Проверяем, не существует ли уже правило с таким productId
            Optional<DynamicRuleEntity> existingRule = dynamicRuleRepository.findByProductId(request.productId());
            if (existingRule.isPresent()) {
                throw new RuntimeException("Rule with productId " + request.productId() + " already exists");
            }

            String ruleJson = objectMapper.writeValueAsString(request.rule());

            DynamicRuleEntity entity = new DynamicRuleEntity(
                    request.productName(),
                    request.productId(),
                    request.productText(),
                    ruleJson
            );

            DynamicRuleEntity savedEntity = dynamicRuleRepository.save(entity);

            // Создаем запись статистики для нового правила
            RuleStatisticEntity statistic = new RuleStatisticEntity(savedEntity.getId());
            ruleStatisticRepository.save(statistic);

            return convertToResponse(savedEntity);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing rule JSON", e);
        }
    }

    public List<DynamicRuleResponse> getAllRules() {
        return dynamicRuleRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public void deleteRule(UUID productId) {
        // Находим правило по productId
        DynamicRuleEntity rule = dynamicRuleRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Rule not found with productId: " + productId));

        // Удаляем статистику
        ruleStatisticRepository.deleteByRuleId(rule.getId());

        // Удаляем правило
        dynamicRuleRepository.deleteByProductId(productId);
    }

    public boolean checkDynamicRule(UUID userId, DynamicRuleEntity rule) {
        try {
            List<RuleQuery> ruleQueries = objectMapper.readValue(
                    rule.getRule(),
                    new TypeReference<List<RuleQuery>>() {}
            );

            // Проверяем все условия правила
            for (RuleQuery query : ruleQueries) {
                boolean result = evaluateQuery(userId, query);
                // Если условие не выполняется и оно не отрицается, правило не срабатывает
                // Если условие выполняется и оно отрицается, правило не срабатывает
                if ((!result && !query.negate()) || (result && query.negate())) {
                    return false;
                }
            }

            // Все условия выполнены - увеличиваем счетчик срабатываний
            incrementRuleStatistic(rule.getId());
            return true;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing rule JSON for rule: " + rule.getId(), e);
        }
    }

    public RuleStatisticResponse getRuleStatistics() {
        List<DynamicRuleEntity> allRules = dynamicRuleRepository.findAll();
        List<RuleStatisticDto> stats = allRules.stream()
                .map(rule -> {
                    Long count = ruleStatisticRepository.findByRuleId(rule.getId())
                            .map(RuleStatisticEntity::getTriggerCount)
                            .orElse(0L);
                    return new RuleStatisticDto(rule.getId(), count);
                })
                .toList();

        return new RuleStatisticResponse(stats);
    }

    private boolean evaluateQuery(UUID userId, RuleQuery query) {
        try {
            boolean result = switch (query.query()) {
                case "USER_OF" -> evaluateUserOf(userId, query);
                case "ACTIVE_USER_OF" -> evaluateActiveUserOf(userId, query);
                case "TRANSACTION_SUM_COMPARE" -> evaluateTransactionSumCompare(userId, query);
                case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> evaluateDepositWithdrawCompare(userId, query);
                default -> throw new IllegalArgumentException("Unknown query type: " + query.query());
            };

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error evaluating query: " + query.query(), e);
        }
    }

    private boolean evaluateUserOf(UUID userId, RuleQuery query) {
        validateArgumentsCount(query, 1);
        String productType = query.arguments().get(0);
        return userDataRepository.hasProductType(userId, productType);
    }

    private boolean evaluateActiveUserOf(UUID userId, RuleQuery query) {
        validateArgumentsCount(query, 1);
        String productType = query.arguments().get(0);
        int transactionCount = userDataRepository.getTransactionCountByProductType(userId, productType);
        return transactionCount >= 5;
    }

    private boolean evaluateTransactionSumCompare(UUID userId, RuleQuery query) {
        validateArgumentsCount(query, 4);
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
        validateArgumentsCount(query, 2);
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
            case "=" -> Math.abs(left - right) < 0.001; // Погрешность для double
            case ">=" -> left >= right;
            case "<=" -> left <= right;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    private void validateArgumentsCount(RuleQuery query, int expectedCount) {
        if (query.arguments().size() != expectedCount) {
            throw new IllegalArgumentException(
                    "Query " + query.query() + " requires " + expectedCount +
                            " arguments, but got " + query.arguments().size()
            );
        }
    }

    private void incrementRuleStatistic(UUID ruleId) {
        RuleStatisticEntity statistic = ruleStatisticRepository.findByRuleId(ruleId)
                .orElse(new RuleStatisticEntity(ruleId));

        statistic.incrementCount();
        ruleStatisticRepository.save(statistic);
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
            throw new RuntimeException("Error converting entity to response for: " + entity.getId(), e);
        }
    }

    // Вспомогательный метод для получения правила по ID
    public Optional<DynamicRuleEntity> getRuleById(UUID ruleId) {
        return dynamicRuleRepository.findById(ruleId);
    }

    // Вспомогательный метод для проверки существования правила
    public boolean ruleExists(UUID productId) {
        return dynamicRuleRepository.findByProductId(productId).isPresent();
    }
}