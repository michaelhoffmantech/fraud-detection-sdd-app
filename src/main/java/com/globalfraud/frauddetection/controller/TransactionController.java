package com.globalfraud.frauddetection.controller;

import com.globalfraud.frauddetection.dto.TransactionRequest;
import com.globalfraud.frauddetection.service.FraudDetectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final FraudDetectionService fraudDetectionService;

    @PostMapping(value = "/validate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ValidationResponse> validateTransaction(
            @Valid @RequestBody TransactionRequest request) {
        var result = fraudDetectionService.processTransaction(request);
        return ResponseEntity.ok(new ValidationResponse(true, result.getRiskScore().doubleValue()));
    }

    private record ValidationResponse(boolean valid, double riskScore) {
    }
}
