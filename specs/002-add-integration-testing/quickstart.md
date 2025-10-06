# Quick Start Guide: Integration Testing with Postman

## Prerequisites
1. Install Postman
2. Install Node.js (for Newman)
3. Install Newman CLI: `npm install -g newman`
4. Clone the repository
5. API access credentials for each environment

## Setup Steps

### 1. Import Postman Collection
1. Open Postman
2. Click "Import"
3. Select `tests/integration/postman/collections/fraud-detection.json`
4. Verify collection is imported successfully

### 2. Configure Environments
1. Import environment files from `tests/integration/postman/environments/`
2. Update environment variables:
   - `baseUrl`
   - `apiKey`
   - Other environment-specific settings
3. Save environment configurations

### 3. Load Test Data
1. Navigate to `tests/integration/postman/data/`
2. Review available test data files
3. Verify data matches your environment

## Running Tests

### Manual Execution (Postman UI)
1. Open Postman
2. Select desired environment
3. Open Collection Runner
4. Choose test data file
5. Run collection
6. Review results

### Automated Execution (Newman CLI)
```powershell
# Run all tests
newman run ./tests/integration/postman/collections/fraud-detection.json -e ./tests/integration/postman/environments/dev.json

# Run with test data
newman run ./tests/integration/postman/collections/fraud-detection.json -e ./tests/integration/postman/environments/dev.json -d ./tests/integration/postman/data/test-cases.json

# Generate detailed report
newman run ./tests/integration/postman/collections/fraud-detection.json -e ./tests/integration/postman/environments/dev.json -r cli,json,html
```

## CI/CD Integration
1. GitHub Actions workflow available at `.github/workflows/integration-tests.yml`
2. Tests run automatically on:
   - Pull requests to main branch
   - Scheduled daily runs
   - Manual trigger
3. Results available in GitHub Actions

## Viewing Reports
1. HTML reports: `tests/integration/newman/reports/html/`
2. JSON reports: `tests/integration/newman/reports/json/`
3. CI/CD reports: GitHub Actions summary

## Troubleshooting
1. Check environment configuration
2. Verify API key validity
3. Ensure test data is correct
4. Review error logs in reports
5. Check network connectivity

## Support
- Documentation: `docs/integration-testing/`
- Issues: GitHub Issues
- Questions: Development Team

## Best Practices
1. Always use environment variables
2. Keep test data up to date
3. Review test reports regularly
4. Update tests when API changes
5. Follow data-driven testing patterns