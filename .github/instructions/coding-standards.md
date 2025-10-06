# Coding Standards and Patterns

## General Principles
- Follow SOLID principles
- Use dependency injection
- Keep methods focused and small
- Use meaningful names
- Document public APIs
- Include proper error handling

## Package Structure
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

## Naming Conventions
- Classes: PascalCase (FraudDetectionService)
- Methods: camelCase (processTransaction)
- Variables: camelCase (riskScore)
- Constants: UPPER_SNAKE_CASE (MAX_RISK_SCORE)
- Packages: lowercase (com.globalfraud)

## Method Standards
- Use descriptive names
- Document parameters
- Include return value descriptions
- Note any exceptions thrown
- Explain side effects
- Document thread safety

## Error Handling
- Use custom exceptions
- Include error context
- Log exceptions appropriately
- Maintain error hierarchy
- Provide clear messages

## Validation
- Use Bean Validation
- Include custom validators
- Validate at boundaries
- Check business rules
- Handle validation errors