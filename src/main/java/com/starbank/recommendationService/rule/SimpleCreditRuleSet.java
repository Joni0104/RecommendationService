package com.starbank.recommendationService.rule;

import com.starbank.recommendationService.model.RecommendationDto;
import com.starbank.recommendationService.repository.UserDataRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRuleSet implements RecommendationRuleSet {

    private static final UUID PRODUCT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    private static final String PRODUCT_NAME = "Простой кредит";
    private static final String PRODUCT_DESCRIPTION = "Откройте мир выгодных кредитов с нами! Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.";

    private final UserDataRepository userDataRepository;

    public SimpleCreditRuleSet(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public Optional<RecommendationDto> checkRules(UUID userId) {
        boolean hasCreditProduct = userDataRepository.hasProductType(userId, "CREDIT");
        boolean depositsGreaterThanSpends = userDataRepository.isDepositsGreaterThanSpends(userId, "DEBIT");
        double debitSpends = userDataRepository.getTotalSpendsByProductType(userId, "DEBIT");
        boolean hasSufficientSpends = debitSpends > 100000.0;

        if (!hasCreditProduct && depositsGreaterThanSpends && hasSufficientSpends) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_DESCRIPTION));
        }

        return Optional.empty();
    }
}