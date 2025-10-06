# API Contracts

## Authentication
All endpoints require an API key provided in the `X-API-Key` header.

## Endpoints

### Submit Transaction for Analysis
```
POST /api/v1/transactions

Request:
{
  "referenceId": string,          // Required: Unique transaction reference
  "amount": number,               // Required: Transaction amount
  "merchantName": string,         // Required: Name of merchant
  "merchantLocation": string,     // Optional: Location of merchant
  "creditCardNumber": string,     // Required: Credit card number (will be hashed)
  "customerName": string,         // Required: Customer's name
  "billingAddress": {
    "street": string,            // Required: Street address
    "city": string,              // Required: City
    "state": string,             // Required: State (2-letter code)
    "zipCode": string,           // Required: ZIP code
    "phone": string              // Required: Phone number
  },
  "deviceInfo": {                // Optional: Device information
    "ipAddress": string,
    "userAgent": string,
    "deviceId": string
  }
}

Response (200 OK):
{
  "transactionId": string,       // UUID of the analyzed transaction
  "referenceId": string,         // Original reference ID
  "riskScore": number,          // Calculated risk score
  "status": string,             // "HIGH_RISK" | "LOW_RISK"
  "analysisTimestamp": string   // ISO-8601 timestamp
}

Error Responses:
400 Bad Request: Invalid input data
401 Unauthorized: Invalid or missing API key
422 Unprocessable Entity: Invalid transaction data
429 Too Many Requests: Rate limit exceeded
500 Internal Server Error: Processing error
```

### Get Transaction Status
```
GET /api/v1/transactions/{transactionId}

Response (200 OK):
{
  "transactionId": string,
  "referenceId": string,
  "riskScore": number,
  "status": string,
  "analysisTimestamp": string,
  "auditTrail": [
    {
      "eventType": string,
      "timestamp": string,
      "details": object
    }
  ]
}

Error Responses:
401 Unauthorized: Invalid or missing API key
404 Not Found: Transaction not found
500 Internal Server Error: Server error
```

### Get Fraud Patterns
```
GET /api/v1/patterns

Response (200 OK):
{
  "patterns": [
    {
      "id": string,
      "name": string,
      "type": string,
      "score": number,
      "enabled": boolean,
      "description": string
    }
  ]
}

Error Responses:
401 Unauthorized: Invalid or missing API key
500 Internal Server Error: Server error
```

### Update Pattern Status
```
PATCH /api/v1/patterns/{patternId}

Request:
{
  "enabled": boolean,
  "score": number          // Optional: Update score
}

Response (200 OK):
{
  "id": string,
  "name": string,
  "type": string,
  "score": number,
  "enabled": boolean,
  "description": string
}

Error Responses:
401 Unauthorized: Invalid or missing API key
404 Not Found: Pattern not found
500 Internal Server Error: Server error
```

### Submit Fraudulent Credit Cards
```
POST /api/v1/fraudulent-cards

Request:
{
  "creditCards": [
    {
      "cardNumber": string,          // Required: Credit card number
      "source": string,              // Required: Source of the report
      "reportedDate": string        // Required: ISO-8601 timestamp
    }
  ]
}

Response (200 OK):
{
  "submitted": number,              // Number of cards successfully submitted
  "duplicates": number,            // Number of cards already in database
  "errors": [                      // Array of cards that couldn't be processed
    {
      "cardNumber": string,
      "error": string
    }
  ]
}

Error Responses:
400 Bad Request: Invalid input data
401 Unauthorized: Invalid or missing API key
422 Unprocessable Entity: Invalid card data
500 Internal Server Error: Server error
```

## Data Types

### Status Values
- `HIGH_RISK`: Transaction matches fraud patterns with high confidence
- `LOW_RISK`: Transaction appears legitimate

### Event Types
- `RECEIVED`: Transaction received for processing
- `ANALYZED`: Fraud analysis completed
- `PATTERN_MATCHED`: Specific fraud pattern matched
- `SCORE_UPDATED`: Risk score was updated
- `STATUS_ASSIGNED`: Final status determined

## Rate Limiting
- Default limit: 30 requests per second per API key
- Configurable based on client requirements
- Headers:
  - `X-RateLimit-Limit`: Request limit
  - `X-RateLimit-Remaining`: Remaining requests
  - `X-RateLimit-Reset`: Time until limit reset