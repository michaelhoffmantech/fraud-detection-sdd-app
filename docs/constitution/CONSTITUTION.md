# Project Constitution - Fraud Detection Service

## 1. Project Overview
This constitution establishes the standards and practices for the Fraud Detection Service, a Spring Boot microservice designed to provide real-time fraud detection capabilities.

## 2. Technology Stack
- Java 21
- Spring Boot 3.2.x
- Gradle 8.4+
- PostgreSQL 15+ (Production)
- H2 Database (Development)
- JUnit 5
- Mockito
- Spring Security
- OpenAPI/Swagger

## 3. Code Standards

### 3.1 General Guidelines
- Follow SOLID principles
- Use dependency injection
- Keep methods focused and small (< 30 lines preferred)
- Use meaningful names for variables, methods, and classes
- Document public APIs using Javadoc
- Use English for all code and comments

### 3.2 Package Structure
```
com.globalfraud.frauddetection/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access
├── domain/        # Entity classes
├── dto/           # Data transfer objects
├── config/        # Configuration classes
├── exception/     # Custom exceptions
└── util/          # Utility classes
```

### 3.3 Naming Conventions
- Classes: PascalCase (e.g., `FraudDetectionService`)
- Methods/Variables: camelCase (e.g., `processTransaction`)
- Constants: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_ATTEMPTS`)
- Packages: lowercase (e.g., `com.globalfraud.frauddetection`)

## 4. Testing Standards

### 4.1 Unit Testing
- Minimum 80% code coverage
- Test naming pattern: `methodName_expectedBehavior_whenCondition`
- One assertion per test (prefer AssertJ)
- Use `@DisplayName` for test descriptions
- Mock external dependencies

### 4.2 Integration Testing
- Use `@SpringBootTest` for full context tests
- Use TestContainers for database tests
- Test all REST endpoints
- Verify database operations
- Test configuration properties

### 4.3 API Testing
- Maintain Postman collection
- Include environment configurations
- Test all HTTP methods
- Verify error responses
- Include authentication tests

## 5. Version Control

### 5.1 Branch Strategy
- main: Production code
- develop: Integration branch
- feature/*: New features
- bugfix/*: Bug fixes
- release/*: Release preparation

### 5.2 Commit Messages
Format:
```
type(scope): description

[optional body]
[optional footer]
```
Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- test: Test changes
- refactor: Code refactoring
- chore: Maintenance tasks

### 5.3 Pull Requests
- Require at least one review
- Must pass all tests
- Must meet code coverage requirements
- Must follow coding standards
- Should include tests

## 6. Documentation

### 6.1 Required Documentation
- README.md with setup instructions
- API documentation (Swagger)
- Architecture Decision Records (ADRs)
- Integration test documentation
- Deployment guides

### 6.2 API Documentation
- Use OpenAPI annotations
- Document all endpoints
- Include request/response examples
- Document error responses
- Keep documentation in sync with code

## 7. Quality Gates

### 7.1 Build Requirements
- All tests must pass
- Code coverage >= 80%
- No critical SonarQube issues
- No security vulnerabilities
- Successful integration tests

### 7.2 Code Review Checklist
- Follows coding standards
- Includes appropriate tests
- Documentation updated
- No security concerns
- Performance considerations addressed
- Error handling implemented

## 8. Security Standards

### 8.1 Code Security
- No hardcoded credentials
- Use environment variables for secrets
- Input validation on all endpoints
- Proper error handling
- Security headers configured

### 8.2 API Security
- Use API key authentication
- Rate limiting implemented
- HTTPS required
- Input sanitization
- Audit logging

## 9. Monitoring and Logging

### 9.1 Logging Standards
- Use SLF4J for logging
- Log levels used appropriately
- Include correlation IDs
- Structured logging format
- No sensitive data in logs

### 9.2 Metrics
- Endpoint response times
- Error rates
- Transaction throughput
- Rule execution metrics
- Database performance

## 10. AI Development Guidelines

### 10.1 GitHub Copilot Usage
- Use Copilot for code generation and completion
- Always review and validate generated code
- Include relevant context in comments for better suggestions
- Document complex business logic before using Copilot
- Test all Copilot-generated code thoroughly

### 10.2 Copilot Instruction Files
- Maintain `.copilot` directory in project root
- Include domain-specific instruction sets
- Document business rules and constraints
- Specify coding patterns and preferences
- Update instructions as project evolves

### 10.3 Best Practices
- Use clear, descriptive comments for better suggestions
- Break down complex tasks into smaller chunks
- Verify generated code meets security standards
- Include test cases in instruction context
- Document any limitations or known issues

### 10.4 Quality Control
- Review all generated code against standards
- Ensure proper error handling
- Verify security implications
- Check for proper logging and monitoring
- Validate against business requirements

## 11. Definition of Done
1. Code complete and reviewed
2. Tests written and passing
3. Documentation updated
4. API documentation current
5. Security requirements met
6. Performance requirements met
7. Logging implemented
8. Metrics configured
9. Integration tests passing
10. Quality gates passed
11. AI-generated code reviewed and validated