package com.starbank.recommendationService.repository;

import com.starbank.recommendationService.model.entity.RuleStatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RuleStatisticRepository extends JpaRepository<RuleStatisticEntity, UUID> {
    Optional<RuleStatisticEntity> findByRuleId(UUID ruleId);
    void deleteByRuleId(UUID ruleId);
}