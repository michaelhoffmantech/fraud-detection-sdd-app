# Postman Collection Contract

## Collection Format
```json
{
  "info": {
    "name": "Fraud Detection API Tests",
    "description": "Integration tests for fraud detection endpoints",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Authentication",
      "item": [
        {
          "name": "Validate API Key",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "X-API-Key",
                "value": "{{apiKey}}",
                "type": "text"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/auth/validate",
              "host": ["{{baseUrl}}"],
              "path": ["auth", "validate"]
            }
          },
          "event": [
            {
              "listen": "test",
              "script": {
                "type": "text/javascript",
                "exec": [
                  "pm.test('Status code is 200', function () {",
                  "    pm.response.to.have.status(200);",
                  "});"
                ]
              }
            }
          ]
        }
      ]
    }
  ]
}
```

## Environment Contract
```json
{
  "id": "fraud-detection-env",
  "name": "Fraud Detection Environment",
  "values": [
    {
      "key": "baseUrl",
      "value": "",
      "type": "string",
      "enabled": true
    },
    {
      "key": "apiKey",
      "value": "",
      "type": "string",
      "enabled": true
    }
  ],
  "_postman_variable_scope": "environment"
}
```

## Test Data Contract
```json
{
  "info": {
    "name": "Fraud Detection Test Data",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Valid Transactions",
      "data": [
        {
          "transactionId": "test-001",
          "amount": 100.00,
          "currency": "USD",
          "merchantId": "MERCH-001",
          "customerId": "CUST-001"
        }
      ]
    },
    {
      "name": "Invalid Transactions",
      "data": [
        {
          "transactionId": "test-002",
          "amount": -100.00,
          "currency": "INVALID",
          "merchantId": "MERCH-002",
          "customerId": "CUST-002"
        }
      ]
    }
  ]
}
```

## Newman CLI Contract
```json
{
  "collection": "./collections/fraud-detection.json",
  "environment": "./environments/dev.json",
  "reporters": ["cli", "json", "html"],
  "reporter": {
    "html": {
      "export": "./reports/html/fraud-detection-report.html",
      "template": "./templates/custom-template.hbs"
    },
    "json": {
      "export": "./reports/json/fraud-detection-report.json"
    }
  },
  "timeout": 5000,
  "timeoutRequest": 3000,
  "delayRequest": 100,
  "bail": false,
  "suppressExitCode": false
}
```

## CI/CD Integration Contract
```yaml
name: Integration Tests

on:
  pull_request:
    branches: [ main ]
  schedule:
    - cron: '0 0 * * *'
  workflow_dispatch:

jobs:
  integration-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '20'
      - run: npm install -g newman
      - run: newman run collections/fraud-detection.json -e environments/dev.json
      - uses: actions/upload-artifact@v3
        with:
          name: test-reports
          path: reports/
```