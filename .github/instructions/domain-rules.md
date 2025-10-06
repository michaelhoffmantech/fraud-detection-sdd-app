# Domain Rules and Business Logic

## Transaction Processing
- All transactions must be validated before processing
- Transactions require merchant ID and customer ID
- Amount must be positive and non-zero
- Currency must be a valid 3-letter code
- Timestamps must be in UTC

## Risk Scoring
- Scores range from 0.0 to 1.0
- High risk threshold: >= 0.7
- Medium risk threshold: >= 0.3
- Low risk: < 0.3
- Scores are averaged across triggered rules

## Transaction States
- PENDING: Initial state
- APPROVED: Risk score acceptable
- REJECTED: High risk score
- FLAGGED_FOR_REVIEW: Medium risk score

## Rule Evaluation
- Rules are evaluated in priority order
- Multiple rules can trigger for one transaction
- Rule configurations are stored as JSON
- Rules can be enabled/disabled
- Each rule type has specific parameters

## Data Requirements
- All monetary values use BigDecimal
- All timestamps use ZonedDateTime
- IDs use UUID
- String fields have maximum lengths
- Required fields cannot be null