package com.globalfraud.frauddetection.controller;

import com.globalfraud.frauddetection.dto.FraudCheckResult;
import com.globalfraud.frauddetection.dto.TransactionRequest;
import com.globalfraud.frauddetection.service.FraudDetectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/fraud")
@RequiredArgsConstructor
public class FraudController {
    private final FraudDetectionService fraudDetectionService;

    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkFraud(
            @Valid @RequestBody TransactionRequest request) {
        FraudCheckResult result = fraudDetectionService.processTransaction(request);
        Map<String, Object> response = new HashMap<>();
        response.put("riskScore", result.getRiskScore());
        return ResponseEntity.ok(response);
    }
}
