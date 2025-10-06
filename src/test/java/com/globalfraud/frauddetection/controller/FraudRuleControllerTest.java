package com.globalfraud.frauddetection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalfraud.frauddetection.domain.FraudRule;
import com.globalfraud.frauddetection.domain.RuleType;
import com.globalfraud.frauddetection.repository.FraudRuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FraudRuleController.class)
class FraudRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FraudRuleRepository fraudRuleRepository;

    @Test
    void getAllRules_ShouldReturnAllRules() throws Exception {
        // Given
        FraudRule rule1 = createTestRule("Rule 1");
        FraudRule rule2 = createTestRule("Rule 2");
        when(fraudRuleRepository.findAll()).thenReturn(List.of(rule1, rule2));

        // When & Then
        mockMvc.perform(get("/api/v1/fraud-rules")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Rule 1"))
                .andExpect(jsonPath("$[1].name").value("Rule 2"));
    }

    @Test
    void getActiveRules_ShouldReturnActiveRules() throws Exception {
        // Given
        FraudRule rule = createTestRule("Active Rule");
        when(fraudRuleRepository.findAllActiveRulesOrderedByPriority()).thenReturn(List.of(rule));

        // When & Then
        mockMvc.perform(get("/api/v1/fraud-rules/active")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Active Rule"));
    }

    @Test
    void createRule_ShouldReturnCreatedRule() throws Exception {
        // Given
        FraudRule rule = createTestRule("New Rule");
        when(fraudRuleRepository.save(any())).thenReturn(rule);

        // When & Then
        mockMvc.perform(post("/api/v1/fraud-rules").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rule))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Rule"));
    }

    @Test
    void updateRule_ShouldReturnUpdatedRule_WhenRuleExists() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        FraudRule rule = createTestRule("Updated Rule");
        rule.setId(id);

        when(fraudRuleRepository.findById(id)).thenReturn(Optional.of(rule));
        when(fraudRuleRepository.save(any())).thenReturn(rule);

        // When & Then
        mockMvc.perform(put("/api/v1/fraud-rules/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rule))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Rule"));
    }

    @Test
    void updateRule_ShouldReturnNotFound_WhenRuleDoesNotExist() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        FraudRule rule = createTestRule("Non-existent Rule");
        when(fraudRuleRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/v1/fraud-rules/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rule))).andExpect(status().isNotFound());
    }

    @Test
    void deleteRule_ShouldReturnNoContent_WhenRuleExists() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        FraudRule rule = createTestRule("Rule to Delete");
        when(fraudRuleRepository.findById(id)).thenReturn(Optional.of(rule));

        // When & Then
        mockMvc.perform(delete("/api/v1/fraud-rules/{id}", id)).andExpect(status().isNoContent());

        verify(fraudRuleRepository).deleteById(id);
    }

    @Test
    void deleteRule_ShouldReturnNotFound_WhenRuleDoesNotExist() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(fraudRuleRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(delete("/api/v1/fraud-rules/{id}", id)).andExpect(status().isNotFound());

        verify(fraudRuleRepository, never()).deleteById(any());
    }

    private FraudRule createTestRule(String name) {
        FraudRule rule = new FraudRule();
        rule.setName(name);
        rule.setRuleType(RuleType.AMOUNT_THRESHOLD);
        rule.setRuleConfig("{\"threshold\": \"1000.00\"}");
        rule.setActive(true);
        rule.setPriority(1);
        return rule;
    }
}
