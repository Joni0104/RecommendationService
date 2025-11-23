package com.starbank.recommendationService.repository;

import com.starbank.recommendationService.model.entity.DynamicRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DynamicRuleRepository extends JpaRepository<DynamicRuleEntity, UUID> {
    void deleteByProductId(UUID productId);
    boolean existsByProductId(UUID productId);
    Optional<DynamicRuleEntity> findByProductId(UUID productId);
}