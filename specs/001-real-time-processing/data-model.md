# Data Model Design

## Entities

### Transaction
```sql
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    reference_id VARCHAR(100) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency VARCHAR(3) NOT NULL CHECK (currency = 'USD'),
    merchant_name VARCHAR(255) NOT NULL,
    merchant_location VARCHAR(255),
    transaction_timestamp TIMESTAMP NOT NULL,
    credit_card_number VARCHAR(255) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    billing_address1 TEXT NOT NULL,
    billing_address2 TEXT NOT NULL,
    billing_city VARCHAR(100) NOT NULL,
    billing_state VARCHAR(2) NOT NULL,
    billing_zip VARCHAR(10) NOT NULL,
    billing_phone VARCHAR(20) NOT NULL,
    shipping_address1 TEXT,
    shipping_address2 TEXT,
    shipping_city VARCHAR(100),
    shipping_state VARCHAR(2),
    shipping_zip VARCHAR(10),
    shipping_phone VARCHAR(20),
    device_info JSONB,
    risk_score INT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT transactions_reference_unique UNIQUE (reference_id)
);

CREATE INDEX idx_transactions_cc_number ON transactions(credit_card_number);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_status ON transactions(status);
```

### FraudPattern
```sql
CREATE TABLE fraud_patterns (
    id UUID PRIMARY KEY,
    pattern_name VARCHAR(100) NOT NULL,
    pattern_type VARCHAR(50) NOT NULL,
    score INT NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fraud_patterns_name_unique UNIQUE (pattern_name)
);
```

### FraudulentCreditCards
```sql
CREATE TABLE fraudulent_credit_cards (
    id UUID PRIMARY KEY,
    credit_card_number VARCHAR(255) NOT NULL,
    reported_date TIMESTAMP NOT NULL,
    source VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fraudulent_cc_number_unique UNIQUE (credit_card_number)
);

CREATE INDEX idx_fraudulent_cc_number ON fraudulent_credit_cards(credit_card_number);
```

### AuditLog
```sql
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    risk_score INT,
    details JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transactions(id)
);

CREATE INDEX idx_audit_transaction_id ON audit_logs(transaction_id);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at);
```

## Relationships
1. Transaction → AuditLog: One-to-Many
   - Each transaction can have multiple audit log entries
   - Audit logs track all decisions and changes

2. Transaction → FraudPattern: Many-to-Many (implicit)
   - Transactions are evaluated against multiple patterns
   - Relationship captured in audit logs

## Indices
- Transactions: credit_card_number, created_at, status
- FraudulentCreditCards: credit_card_number
- AuditLog: transaction_id, created_at

## Notes
1. All timestamps use UTC
2. JSONB used for flexible storage of device info and audit details
3. UUIDs used for all primary keys
4. Proper indexing for performance
5. Check constraint ensures USD currency only
6. Audit trail maintained through audit_logs table