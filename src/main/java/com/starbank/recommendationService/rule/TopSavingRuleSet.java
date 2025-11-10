package com.starbank.recommendationService.rule;

import com.starbank.recommendationService.model.RecommendationDto;
import com.starbank.recommendationService.repository.UserDataRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRuleSet implements RecommendationRuleSet {

    private static final UUID PRODUCT_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    private static final String PRODUCT_NAME = "Top Saving";
    private static final String PRODUCT_DESCRIPTION = "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!";

    private final UserDataRepository userDataRepository;

    public TopSavingRuleSet(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public Optional<RecommendationDto> checkRules(UUID userId) {
        boolean hasDebitProduct = userDataRepository.hasProductType(userId, "DEBIT");
        double debitDeposits = userDataRepository.getTotalDepositsByProductType(userId, "DEBIT");
        double savingDeposits = userDataRepository.getTotalDepositsByProductType(userId, "SAVING");
        boolean hasSufficientDeposits = debitDeposits >= 50000.0 || savingDeposits >= 50000.0;
        boolean depositsGreaterThanSpends = userDataRepository.isDepositsGreaterThanSpends(userId, "DEBIT");

        if (hasDebitProduct && hasSufficientDeposits && depositsGreaterThanSpends) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_DESCRIPTION));
        }

        return Optional.empty();
    }
}