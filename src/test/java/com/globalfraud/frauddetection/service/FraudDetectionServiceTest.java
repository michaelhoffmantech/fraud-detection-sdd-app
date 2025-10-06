package com.globalfraud.frauddetection.service;

import com.globalfraud.frauddetection.domain.FraudRule;
import com.globalfraud.frauddetection.domain.Transaction;
import com.globalfraud.frauddetection.domain.TransactionStatus;
import com.globalfraud.frauddetection.dto.FraudCheckResult;
import com.globalfraud.frauddetection.dto.TransactionRequest;
import com.globalfraud.frauddetection.repository.FraudRuleRepository;
import com.globalfraud.frauddetection.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FraudRuleRepository fraudRuleRepository;

    @Mock
    private RuleEvaluator ruleEvaluator;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    private FraudDetectionService fraudDetectionService;
    private TransactionRequest testRequest;
    private List<FraudRule> testRules;

    @BeforeEach
    void setUp() {
        fraudDetectionService = new FraudDetectionService(transactionRepository,
                fraudRuleRepository, ruleEvaluator);

        testRequest = new TransactionRequest();
        testRequest.setMerchantId("MERCHANT001");
        testRequest.setCustomerId("CUSTOMER001");
        testRequest.setAmount(new BigDecimal("1000.00"));
        testRequest.setCurrency("USD");
        testRequest.setTransactionTimestamp(ZonedDateTime.now());

        FraudRule rule1 = new FraudRule();
        rule1.setName("High Amount Rule");
        FraudRule rule2 = new FraudRule();
        rule2.setName("Velocity Rule");
        testRules = List.of(rule1, rule2);
    }

    @Test
    void processTransaction_ShouldApproveTransaction_WhenNoRulesTriggered() {
        // Given
        when(fraudRuleRepository.findAllActiveRulesOrderedByPriority()).thenReturn(testRules);
        when(ruleEvaluator.evaluate(any(), any())).thenReturn(false);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // When
        FraudCheckResult result = fraudDetectionService.processTransaction(testRequest);

        // Then
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.APPROVED);
        assertThat(result.getRiskScore()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getTriggeredRules()).isEmpty();

        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();
        assertThat(savedTransaction.getStatus()).isEqualTo(TransactionStatus.APPROVED);
    }

    @Test
    void processTransaction_ShouldRejectTransaction_WhenHighRiskRulesTriggered() {
        // Given
        when(fraudRuleRepository.findAllActiveRulesOrderedByPriority()).thenReturn(testRules);
        when(ruleEvaluator.evaluate(any(), any())).thenReturn(true);
        when(ruleEvaluator.calculateRiskScore(any(), any())).thenReturn(0.9);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // When
        FraudCheckResult result = fraudDetectionService.processTransaction(testRequest);

        // Then
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.REJECTED);
        assertThat(result.getRiskScore().doubleValue()).isGreaterThanOrEqualTo(0.7);
        assertThat(result.getTriggeredRules()).containsExactly("High Amount Rule", "Velocity Rule");

        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();
        assertThat(savedTransaction.getStatus()).isEqualTo(TransactionStatus.REJECTED);
    }

    @Test
    void processTransaction_ShouldFlagForReview_WhenMediumRiskRulesTriggered() {
        // Given
        when(fraudRuleRepository.findAllActiveRulesOrderedByPriority()).thenReturn(testRules);
        when(ruleEvaluator.evaluate(any(), any())).thenReturn(true);
        when(ruleEvaluator.calculateRiskScore(any(), any())).thenReturn(0.5);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // When
        FraudCheckResult result = fraudDetectionService.processTransaction(testRequest);

        // Then
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.FLAGGED_FOR_REVIEW);
        assertThat(result.getRiskScore().doubleValue()).isBetween(0.3, 0.7);
        assertThat(result.getTriggeredRules()).hasSize(2);

        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();
        assertThat(savedTransaction.getStatus()).isEqualTo(TransactionStatus.FLAGGED_FOR_REVIEW);
    }
}
