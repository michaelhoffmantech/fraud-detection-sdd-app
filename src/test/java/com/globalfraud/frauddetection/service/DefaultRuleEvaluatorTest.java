package com.globalfraud.frauddetection.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalfraud.frauddetection.domain.FraudRule;
import com.globalfraud.frauddetection.domain.RuleType;
import com.globalfraud.frauddetection.domain.Transaction;
import com.globalfraud.frauddetection.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultRuleEvaluatorTest {

    @Mock
    private TransactionRepository transactionRepository;

    private ObjectMapper objectMapper;
    private DefaultRuleEvaluator ruleEvaluator;
    private Transaction testTransaction;
    private FraudRule testRule;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        ruleEvaluator = new DefaultRuleEvaluator(transactionRepository, objectMapper);

        testTransaction = new Transaction();
        testTransaction.setAmount(new BigDecimal("1000.00"));
        testTransaction.setMerchantId("MERCHANT001");
        testTransaction.setCustomerId("CUSTOMER001");
        testTransaction.setTransactionTimestamp(ZonedDateTime.now());

        testRule = new FraudRule();
        testRule.setRuleType(RuleType.AMOUNT_THRESHOLD);
    }

    @Test
    void evaluateAmountThresholdRule_ShouldReturnTrue_WhenAmountExceedsThreshold()
            throws Exception {
        // Given
        testRule.setRuleConfig("{\"threshold\": \"500.00\"}");

        // When
        boolean result = ruleEvaluator.evaluate(testTransaction, testRule);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void evaluateVelocityCheck_ShouldReturnTrue_WhenTooManyTransactions() {
        // Given
        testRule.setRuleType(RuleType.VELOCITY_CHECK);
        testRule.setRuleConfig("{\"maxTransactions\": 3, \"timeWindowMinutes\": 60}");

        List<Transaction> recentTransactions =
                List.of(new Transaction(), new Transaction(), new Transaction(), new Transaction());

        when(transactionRepository.findTransactionsByCustomerInTimeWindow(eq("CUSTOMER001"), any(),
                any())).thenReturn(recentTransactions);

        // When
        boolean result = ruleEvaluator.evaluate(testTransaction, testRule);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void evaluateVelocityCheck_ShouldReturnFalse_WhenFewTransactions() {
        // Given
        testRule.setRuleType(RuleType.VELOCITY_CHECK);
        testRule.setRuleConfig("{\"maxTransactions\": 3, \"timeWindowMinutes\": 60}");

        when(transactionRepository.findTransactionsByCustomerInTimeWindow(eq("CUSTOMER001"), any(),
                any())).thenReturn(Collections.emptyList());

        // When
        boolean result = ruleEvaluator.evaluate(testTransaction, testRule);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void calculateRiskScore_ShouldReturnHighScore_WhenAmountMuchHigherThanThreshold()
            throws Exception {
        // Given
        testRule.setRuleConfig("{\"threshold\": \"100.00\"}");

        // When
        double score = ruleEvaluator.calculateRiskScore(testTransaction, testRule);

        // Then
        assertThat(score).isGreaterThanOrEqualTo(0.8);
    }
}
