package com.starbank.recommendationService.rule;

import com.starbank.recommendationService.model.RecommendationDto;
import com.starbank.recommendationService.repository.UserDataRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500RuleSet implements RecommendationRuleSet {

    private static final UUID PRODUCT_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private static final String PRODUCT_NAME = "Invest 500";
    private static final String PRODUCT_DESCRIPTION = "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!";

    private final UserDataRepository userDataRepository;

    public Invest500RuleSet(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public Optional<RecommendationDto> checkRules(UUID userId) {
        boolean hasDebitProduct = userDataRepository.hasProductType(userId, "DEBIT");
        boolean hasInvestProduct = userDataRepository.hasProductType(userId, "INVEST");
        double savingDeposits = userDataRepository.getTotalDepositsByProductType(userId, "SAVING");
        boolean hasSufficientSavingDeposits = savingDeposits > 1000.0;

        if (hasDebitProduct && !hasInvestProduct && hasSufficientSavingDeposits) {
            return Optional.of(new RecommendationDto(PRODUCT_NAME, PRODUCT_ID, PRODUCT_DESCRIPTION));
        }

        return Optional.empty();
    }
}