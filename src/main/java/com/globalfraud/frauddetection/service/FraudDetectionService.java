package com.globalfraud.frauddetection.service;

import com.globalfraud.frauddetection.domain.FraudRule;
import com.globalfraud.frauddetection.domain.Transaction;
import com.globalfraud.frauddetection.domain.TransactionStatus;
import com.globalfraud.frauddetection.dto.FraudCheckResult;
import com.globalfraud.frauddetection.dto.TransactionRequest;
import com.globalfraud.frauddetection.repository.FraudRuleRepository;
import com.globalfraud.frauddetection.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {
    private final TransactionRepository transactionRepository;
    private final FraudRuleRepository fraudRuleRepository;
    private final RuleEvaluator ruleEvaluator;
    
    private static final BigDecimal HIGH_RISK_THRESHOLD = new BigDecimal("0.7");
    private static final BigDecimal MEDIUM_RISK_THRESHOLD = new BigDecimal("0.3");

    @Transactional
    public FraudCheckResult processTransaction(TransactionRequest request) {
        Transaction transaction = createTransaction(request);
        List<FraudRule> activeRules = fraudRuleRepository.findAllActiveRulesOrderedByPriority();
        
        List<String> triggeredRules = new ArrayList<>();
        BigDecimal totalRiskScore = BigDecimal.ZERO;
        int ruleCount = 0;
        
        for (FraudRule rule : activeRules) {
            if (ruleEvaluator.evaluate(transaction, rule)) {
                triggeredRules.add(rule.getName());
                totalRiskScore = totalRiskScore.add(
                    BigDecimal.valueOf(ruleEvaluator.calculateRiskScore(transaction, rule))
                );
                ruleCount++;
            }
        }
        
        BigDecimal averageRiskScore = ruleCount > 0 
            ? totalRiskScore.divide(BigDecimal.valueOf(ruleCount), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
            
        TransactionStatus status = determineTransactionStatus(averageRiskScore);
        transaction.setRiskScore(averageRiskScore);
        transaction.setStatus(status);
        
        transactionRepository.save(transaction);
        
        return FraudCheckResult.builder()
            .transactionId(transaction.getId())
            .status(status)
            .riskScore(averageRiskScore)
            .triggeredRules(triggeredRules)
            .message(generateResultMessage(status, averageRiskScore))
            .build();
    }
    
    private Transaction createTransaction(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setMerchantId(request.getMerchantId());
        transaction.setCustomerId(request.getCustomerId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setTransactionTimestamp(request.getTransactionTimestamp());
        transaction.setStatus(TransactionStatus.PENDING);
        return transaction;
    }
    
    private TransactionStatus determineTransactionStatus(BigDecimal riskScore) {
        if (riskScore.compareTo(HIGH_RISK_THRESHOLD) >= 0) {
            return TransactionStatus.REJECTED;
        } else if (riskScore.compareTo(MEDIUM_RISK_THRESHOLD) >= 0) {
            return TransactionStatus.FLAGGED_FOR_REVIEW;
        } else {
            return TransactionStatus.APPROVED;
        }
    }
    
    private String generateResultMessage(TransactionStatus status, BigDecimal riskScore) {
        return switch (status) {
            case REJECTED -> String.format("Transaction rejected due to high risk score: %.2f", riskScore);
            case FLAGGED_FOR_REVIEW -> String.format("Transaction requires manual review. Risk score: %.2f", riskScore);
            case APPROVED -> String.format("Transaction approved. Risk score: %.2f", riskScore);
            default -> "Transaction is being processed";
        };
    }
}