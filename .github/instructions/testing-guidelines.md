# Testing Guidelines

## Test Structure
- Follow given/when/then pattern
- One assertion per test
- Clear test names
- Use @DisplayName
- Include proper setup/teardown

## Naming Convention
```
methodName_expectedBehavior_whenCondition
```

Examples:
- processTransaction_shouldRejectTransaction_whenHighRiskDetected
- evaluateRule_shouldReturnTrue_whenThresholdExceeded

## Required Test Cases
- Happy path
- Edge cases
- Error conditions
- Boundary values
- Timeout scenarios

## Mock Usage
- Mock external dependencies
- Use @MockBean for Spring contexts
- Verify mock interactions
- Set up proper responses
- Handle exceptions

## Test Data
- Use meaningful test data
- Create builder methods
- Use test factories
- Avoid magic numbers
- Document data purpose

## Integration Tests
- Use @SpringBootTest
- Test full flows
- Use TestContainers
- Clean up test data
- Verify database state

## Example Test
```java
@Test
@DisplayName("Should reject transaction when risk score is high")
void processTransaction_shouldRejectTransaction_whenHighRiskDetected() {
    // Given
    TransactionRequest request = createHighRiskTransaction();
    when(ruleEvaluator.evaluate(any(), any())).thenReturn(true);
    when(ruleEvaluator.calculateRiskScore(any(), any())).thenReturn(0.8);

    // When
    FraudCheckResult result = service.processTransaction(request);

    // Then
    assertThat(result.getStatus()).isEqualTo(TransactionStatus.REJECTED);
    assertThat(result.getRiskScore()).isGreaterThanOrEqualTo(BigDecimal.valueOf(0.7));
}
```