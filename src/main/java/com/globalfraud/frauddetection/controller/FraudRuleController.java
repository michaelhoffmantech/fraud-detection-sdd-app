package com.globalfraud.frauddetection.controller;

import com.globalfraud.frauddetection.domain.FraudRule;
import com.globalfraud.frauddetection.repository.FraudRuleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fraud-rules")
@RequiredArgsConstructor
@Tag(name = "Fraud Rules", description = "API endpoints for managing fraud detection rules")
public class FraudRuleController {

    private final FraudRuleRepository fraudRuleRepository;

    @GetMapping
    @Operation(summary = "Get all fraud rules")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rules retrieved successfully")})
    public ResponseEntity<List<FraudRule>> getAllRules() {
        return ResponseEntity.ok(fraudRuleRepository.findAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active fraud rules")
    @ApiResponses({@ApiResponse(responseCode = "200",
            description = "Active rules retrieved successfully")})
    public ResponseEntity<List<FraudRule>> getActiveRules() {
        return ResponseEntity.ok(fraudRuleRepository.findAllActiveRulesOrderedByPriority());
    }

    @PostMapping
    @Operation(summary = "Create a new fraud rule")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Rule created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rule data")})
    public ResponseEntity<FraudRule> createRule(@Valid @RequestBody FraudRule rule) {
        return ResponseEntity.ok(fraudRuleRepository.save(rule));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing fraud rule")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Rule updated successfully"),
            @ApiResponse(responseCode = "404", description = "Rule not found")})
    public ResponseEntity<FraudRule> updateRule(@PathVariable UUID id,
            @Valid @RequestBody FraudRule rule) {
        return fraudRuleRepository.findById(id).map(existingRule -> {
            rule.setId(id);
            return ResponseEntity.ok(fraudRuleRepository.save(rule));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a fraud rule")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Rule deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Rule not found")})
    public ResponseEntity<Void> deleteRule(@PathVariable UUID id) {
        return fraudRuleRepository.findById(id).map(rule -> {
            fraudRuleRepository.deleteById(id);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
