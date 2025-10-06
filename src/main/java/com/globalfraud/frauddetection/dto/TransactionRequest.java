package com.globalfraud.frauddetection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class TransactionRequest {
    @NotBlank(message = "Merchant ID is required")
    private String merchantId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    private ZonedDateTime transactionTimestamp;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;
}
