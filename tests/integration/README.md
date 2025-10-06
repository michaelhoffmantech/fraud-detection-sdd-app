# Integration Testing Documentation

## Overview
This directory contains the integration test suite for the Fraud Detection API, implemented using Postman collections and Newman for automation.

## Test Structure
```
tests/integration/
├── postman/
│   ├── collections/       # Postman test collections
│   ├── environments/      # Environment configurations
│   └── data/             # Test data files
└── newman/
    ├── scripts/          # Test execution scripts
    └── reports/          # Test execution reports
```

## Test Collection
The test collection is organized into the following sections:

1. Authentication
   - API key validation
   - Invalid key handling

2. Transaction Validation
   - Single transaction validation
   - Batch transaction processing

3. Fraud Detection
   - Risk assessment
   - Score calculation

4. Reporting
   - Results retrieval
   - Batch reporting

## Test Data
Test data is organized into scenarios:
- Valid transactions (standard amounts)
- Valid transactions (large amounts)
- Invalid transactions (validation failures)
- Suspicious transactions (high risk patterns)

## Environment Setup

### Prerequisites

1. Node.js
   - Visit https://nodejs.org/
   - Download and install Node.js LTS
   - Restart your terminal
   - Verify by running: `node --version`

2. Newman
   - Open a terminal
   - Run: `npm install -g newman newman-reporter-htmlextra`
   - Verify by running: `newman --version`

### Validating Environment

Run the environment validation script:
```powershell
.\newman\scripts\run-tests.ps1 -ValidateOnly
```

## Running Tests

### Using Postman UI
1. Import the collection from `postman/collections/fraud-detection.json`
2. Import the environment file from `postman/environments/`
3. Load test data from `postman/data/test-cases.json`
4. Run the collection using Postman's Collection Runner

### Using Newman CLI
```powershell
# Run tests with local environment
./newman/scripts/run-tests.ps1 -Environment local

# Run tests with specific environment and generate report
./newman/scripts/run-tests.ps1 -Environment dev -GenerateReport
```

## CI/CD Integration
Tests are automatically run:
- On pull requests to main branch
- Daily at midnight UTC
- Manually via GitHub Actions

## Reports
Test reports are generated in:
- HTML format: `newman/reports/report-{environment}.html`
- CLI output during execution

## Troubleshooting

### Common Issues
1. API Key Invalid
   - Verify environment configuration
   - Check key expiration
   - Ensure proper permissions

2. Connection Failed
   - Check environment URL
   - Verify network access
   - Check service status

3. Test Data Issues
   - Validate JSON format
   - Check required fields
   - Verify data matches schema

### Getting Help
- Review error logs in reports
- Check service status page
- Contact development team