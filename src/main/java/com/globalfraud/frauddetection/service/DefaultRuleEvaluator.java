package com.globalfraud.frauddetection.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalfraud.frauddetection.domain.FraudRule;
import com.globalfraud.frauddetection.domain.RuleType;
import com.globalfraud.frauddetection.domain.Transaction;
import com.globalfraud.frauddetection.domain.TransactionStatus;
import com.globalfraud.frauddetection.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultRuleEvaluator implements RuleEvaluator {
    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;

    @Override
    public boolean evaluate(Transaction transaction, FraudRule rule) {
        try {
            JsonNode config = objectMapper.readTree(rule.getRuleConfig());

            return switch (rule.getRuleType()) {
                case AMOUNT_THRESHOLD -> evaluateAmountThreshold(transaction, config);
                case VELOCITY_CHECK -> evaluateVelocityCheck(transaction, config);
                case MERCHANT_RISK -> evaluateMerchantRisk(transaction, config);
                case CUSTOMER_HISTORY -> evaluateCustomerHistory(transaction, config);
                default -> false;
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse rule configuration", e);
        }
    }

    @Override
    public double calculateRiskScore(Transaction transaction, FraudRule rule) {
        try {
            JsonNode config = objectMapper.readTree(rule.getRuleConfig());

            return switch (rule.getRuleType()) {
                case AMOUNT_THRESHOLD -> calculateAmountThresholdScore(transaction, config);
                case VELOCITY_CHECK -> calculateVelocityScore(transaction, config);
                case MERCHANT_RISK -> calculateMerchantRiskScore(transaction, config);
                case CUSTOMER_HISTORY -> calculateCustomerHistoryScore(transaction, config);
                default -> 0.0;
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse rule configuration", e);
        }
    }

    private boolean evaluateAmountThreshold(Transaction transaction, JsonNode config) {
        BigDecimal threshold = new BigDecimal(config.get("threshold").asText());
        return transaction.getAmount().compareTo(threshold) > 0;
    }

    private boolean evaluateVelocityCheck(Transaction transaction, JsonNode config) {
        int maxTransactions = config.get("maxTransactions").asInt();
        int timeWindowMinutes = config.get("timeWindowMinutes").asInt();

        ZonedDateTime endTime = transaction.getTransactionTimestamp();
        ZonedDateTime startTime = endTime.minus(timeWindowMinutes, ChronoUnit.MINUTES);

        List<Transaction> recentTransactions =
                transactionRepository.findTransactionsByCustomerInTimeWindow(
                        transaction.getCustomerId(), startTime, endTime);

        return recentTransactions.size() >= maxTransactions;
    }

    private boolean evaluateMerchantRisk(Transaction transaction, JsonNode config) {
        BigDecimal merchantRiskThreshold =
                new BigDecimal(config.get("merchantRiskThreshold").asText());
        int timeWindowHours = config.get("timeWindowHours").asInt();

        ZonedDateTime endTime = transaction.getTransactionTimestamp();
        ZonedDateTime startTime = endTime.minus(timeWindowHours, ChronoUnit.HOURS);

        List<Transaction> merchantTransactions =
                transactionRepository.findTransactionsByMerchantInTimeWindow(
                        transaction.getMerchantId(), startTime, endTime);

        return calculateMerchantRiskFactor(merchantTransactions)
                .compareTo(merchantRiskThreshold) > 0;
    }

    private boolean evaluateCustomerHistory(Transaction transaction, JsonNode config) {
        BigDecimal averageTransactionThreshold =
                new BigDecimal(config.get("averageTransactionThreshold").asText());
        List<Transaction> customerTransactions =
                transactionRepository.findByCustomerId(transaction.getCustomerId());

        if (customerTransactions.isEmpty()) {
            return true;
        }

        BigDecimal avgAmount = customerTransactions.stream().map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(customerTransactions.size()), 2, BigDecimal.ROUND_HALF_UP);

        return transaction.getAmount()
                .compareTo(avgAmount.multiply(averageTransactionThreshold)) > 0;
    }

    private double calculateAmountThresholdScore(Transaction transaction, JsonNode config) {
        BigDecimal threshold = new BigDecimal(config.get("threshold").asText());
        if (transaction.getAmount().compareTo(threshold) <= 0) {
            return 0.0;
        }
        return Math.min(1.0,
                transaction.getAmount().divide(threshold, 2, RoundingMode.HALF_UP).doubleValue()
                        - 1.0);
    }

    private double calculateVelocityScore(Transaction transaction, JsonNode config) {
        int maxTransactions = config.get("maxTransactions").asInt();
        int timeWindowMinutes = config.get("timeWindowMinutes").asInt();

        ZonedDateTime endTime = transaction.getTransactionTimestamp();
        ZonedDateTime startTime = endTime.minus(timeWindowMinutes, ChronoUnit.MINUTES);

        List<Transaction> recentTransactions =
                transactionRepository.findTransactionsByCustomerInTimeWindow(
                        transaction.getCustomerId(), startTime, endTime);

        return Math.min(1.0, (double) recentTransactions.size() / maxTransactions);
    }

    private double calculateMerchantRiskScore(Transaction transaction, JsonNode config) {
        BigDecimal merchantRiskThreshold =
                new BigDecimal(config.get("merchantRiskThreshold").asText());
        int timeWindowHours = config.get("timeWindowHours").asInt();

        ZonedDateTime endTime = transaction.getTransactionTimestamp();
        ZonedDateTime startTime = endTime.minus(timeWindowHours, ChronoUnit.HOURS);

        List<Transaction> merchantTransactions =
                transactionRepository.findTransactionsByMerchantInTimeWindow(
                        transaction.getMerchantId(), startTime, endTime);

        return Math.min(1.0, calculateMerchantRiskFactor(merchantTransactions)
                .divide(merchantRiskThreshold, 2, RoundingMode.HALF_UP).doubleValue());
    }

    private double calculateCustomerHistoryScore(Transaction transaction, JsonNode config) {
        BigDecimal averageTransactionThreshold =
                new BigDecimal(config.get("averageTransactionThreshold").asText());
        List<Transaction> customerTransactions =
                transactionRepository.findByCustomerId(transaction.getCustomerId());

        if (customerTransactions.isEmpty()) {
            return 1.0;
        }

        BigDecimal avgAmount = customerTransactions.stream().map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(customerTransactions.size()), 2, BigDecimal.ROUND_HALF_UP);

        return Math.min(1.0, transaction.getAmount()
                .divide(avgAmount.multiply(averageTransactionThreshold), 2, RoundingMode.HALF_UP)
                .doubleValue() - 1.0);
    }

    private BigDecimal calculateMerchantRiskFactor(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long rejectedCount = transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.REJECTED).count();

        return new BigDecimal(rejectedCount).divide(new BigDecimal(transactions.size()), 2,
                RoundingMode.HALF_UP);
    }
}
