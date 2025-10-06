package com.globalfraud.frauddetection.dto;

import com.globalfraud.frauddetection.domain.TransactionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class FraudCheckResult {
    private UUID transactionId;
    private TransactionStatus status;
    private BigDecimal riskScore;
    private List<String> triggeredRules;
    private String message;
}
