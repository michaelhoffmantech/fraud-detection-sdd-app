# Local Development Quick Start

## Prerequisites

1. Development Tools
   - Java 21 or higher
   - Gradle 8.5+
   - Docker Desktop
   - AWS CLI configured
   - Terraform 1.5+
   - Git

2. Environment Setup
   ```bash
   # Clone the repository
   git clone https://github.com/michaelhoffmantech/fraud-detection-sdd-app.git
   cd fraud-detection-sdd-app

   # Build the application
   ./gradlew clean build

   # Start local services (H2 database will be auto-configured)
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

## Development Database

The application uses H2 in-memory database for local development:
- Console URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:frauddb
- Username: sa
- Password: password

## Local Testing

1. Run unit tests:
   ```bash
   ./gradlew test
   ```

2. Run integration tests:
   ```bash
   ./gradlew integrationTest
   ```

3. Test the API locally:
   ```bash
   # Get an API key (for local development)
   curl -X POST http://localhost:8080/api/v1/debug/apikey

   # Test the fraud detection API
   curl -X POST http://localhost:8080/api/v1/transactions \
     -H "Content-Type: application/json" \
     -H "X-API-Key: your-api-key" \
     -d '{
       "referenceId": "test-123",
       "amount": 100.00,
       "merchantName": "Test Merchant",
       "creditCardNumber": "4111111111111111",
       "customerName": "John Doe",
       "billingAddress": {
         "street": "123 Test St",
         "city": "Testville",
         "state": "TX",
         "zipCode": "12345",
         "phone": "555-0123"
       }
     }'
   ```

## Infrastructure Development

1. Initialize Terraform:
   ```bash
   cd terraform
   terraform init
   ```

2. Plan infrastructure changes:
   ```bash
   terraform plan -var-file=dev.tfvars
   ```

3. Local Development Stack:
   ```bash
   # Start local development stack (if needed)
   docker-compose up -d
   ```

## Configuration

### Application Properties
- `application.yml` - Main configuration
- `application-local.yml` - Local development settings
- `application-dev.yml` - Development environment settings

### Environment Variables
Required for local development:
```bash
export SPRING_PROFILES_ACTIVE=local
export APP_API_KEY_SECRET=your-local-secret
```

### Logging
- Logs written to `./logs/application.log`
- Configure in `logback-spring.xml`

## Common Tasks

### Adding a New Fraud Pattern
1. Create a migration in `src/main/resources/db/migration`
2. Implement the pattern logic in `src/main/java/com/globalfraud/patterns`
3. Add unit tests
4. Update documentation

### Updating API Contract
1. Modify OpenAPI specification in `src/main/resources/api`
2. Generate updated client code
3. Update version number
4. Update documentation

### Database Changes
1. Create a new Flyway migration
2. Update the entity classes
3. Update repository tests
4. Run integration tests

## Troubleshooting

### Common Issues
1. Database connection issues
   - Check H2 console is accessible
   - Verify application.yml configuration

2. Build failures
   - Clean Gradle cache: `./gradlew clean`
   - Refresh dependencies: `./gradlew --refresh-dependencies`
   - Show outdated dependencies: `./gradlew dependencyUpdates`

3. Test failures
   - Run specific test: `./mvnw test -Dtest=TestClassName`
   - Debug test: `./mvnw test -Dmaven.surefire.debug`

### Getting Help
- Check the project wiki
- Review error logs in `./logs`
- Contact the development team