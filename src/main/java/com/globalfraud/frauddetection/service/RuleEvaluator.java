package com.globalfraud.frauddetection.service;

import com.globalfraud.frauddetection.domain.FraudRule;
import com.globalfraud.frauddetection.domain.Transaction;

public interface RuleEvaluator {
    boolean evaluate(Transaction transaction, FraudRule rule);

    double calculateRiskScore(Transaction transaction, FraudRule rule);
}
