package com.starbank.recommendationService.model.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "rule_statistics")
public class RuleStatisticEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    @Column(name = "trigger_count", nullable = false)
    private Long triggerCount = 0L;

    public RuleStatisticEntity() {}

    public RuleStatisticEntity(UUID ruleId) {
        this.ruleId = ruleId;
    }

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getRuleId() { return ruleId; }
    public void setRuleId(UUID ruleId) { this.ruleId = ruleId; }
    public Long getTriggerCount() { return triggerCount; }
    public void setTriggerCount(Long triggerCount) { this.triggerCount = triggerCount; }

    public void incrementCount() {
        this.triggerCount++;
    }
}