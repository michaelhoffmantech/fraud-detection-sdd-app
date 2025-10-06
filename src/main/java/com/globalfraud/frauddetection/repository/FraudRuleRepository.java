package com.globalfraud.frauddetection.repository;

import com.globalfraud.frauddetection.domain.FraudRule;
import com.globalfraud.frauddetection.domain.RuleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FraudRuleRepository extends JpaRepository<FraudRule, UUID> {

    List<FraudRule> findByActiveTrue();

    List<FraudRule> findByActiveTrueAndRuleType(RuleType ruleType);

    @Query("SELECT fr FROM FraudRule fr WHERE fr.active = true ORDER BY fr.priority ASC")
    List<FraudRule> findAllActiveRulesOrderedByPriority();
}
