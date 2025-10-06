package com.globalfraud.frauddetection.controller;

import com.globalfraud.frauddetection.dto.FraudCheckResult;
import com.globalfraud.frauddetection.dto.TransactionRequest;
import com.globalfraud.frauddetection.service.FraudDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraud-detection")
@RequiredArgsConstructor
@Tag(name = "Fraud Detection", description = "API endpoints for fraud detection")
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    @PostMapping("/check")
    @Operation(summary = "Check transaction for fraud",
            description = "Analyzes a transaction for potential fraud using configured rules")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    public ResponseEntity<FraudCheckResult> checkTransaction(
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(fraudDetectionService.processTransaction(request));
    }
}
