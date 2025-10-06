package com.globalfraud.frauddetection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalfraud.frauddetection.domain.TransactionStatus;
import com.globalfraud.frauddetection.dto.FraudCheckResult;
import com.globalfraud.frauddetection.dto.TransactionRequest;
import com.globalfraud.frauddetection.service.FraudDetectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FraudDetectionController.class)
class FraudDetectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FraudDetectionService fraudDetectionService;

    @Test
    void checkTransaction_ShouldReturnFraudCheckResult_WhenValidRequest() throws Exception {
        // Given
        TransactionRequest request = new TransactionRequest();
        request.setMerchantId("MERCHANT001");
        request.setCustomerId("CUSTOMER001");
        request.setAmount(new BigDecimal("1000.00"));
        request.setCurrency("USD");
        request.setTransactionTimestamp(ZonedDateTime.now());

        UUID transactionId = UUID.randomUUID();
        FraudCheckResult expectedResult = FraudCheckResult.builder().transactionId(transactionId)
                .status(TransactionStatus.APPROVED).riskScore(new BigDecimal("0.2"))
                .triggeredRules(List.of()).message("Transaction approved. Risk score: 0.20")
                .build();

        when(fraudDetectionService.processTransaction(any())).thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(
                post("/api/v1/fraud-detection/check").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(transactionId.toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.riskScore").value("0.2"))
                .andExpect(jsonPath("$.triggeredRules").isEmpty())
                .andExpect(jsonPath("$.message").value("Transaction approved. Risk score: 0.20"));
    }

    @Test
    void checkTransaction_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        TransactionRequest request = new TransactionRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(
                post("/api/v1/fraud-detection/check").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
