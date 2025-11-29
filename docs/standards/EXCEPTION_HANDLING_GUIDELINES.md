# Exception Handling Guidelines

This document provides guidelines for exception handling in the Brightspot codebase. These guidelines should be followed when writing new code and when refactoring existing code.

## Table of Contents

1. [Exception Hierarchy](#exception-hierarchy)
2. [When to Use Which Exception](#when-to-use-which-exception)
3. [Exception Handling Patterns](#exception-handling-patterns)
4. [Best Practices](#best-practices)
5. [Common Patterns](#common-patterns)
6. [Anti-Patterns to Avoid](#anti-patterns-to-avoid)

---

## Exception Hierarchy

The Brightspot application uses a hierarchical exception structure:

```
BrightspotException (base checked exception)
├── EncryptionException
├── DatabaseOperationException
├── ConfigurationException
└── ValidationException
```

### Base Exception: `BrightspotException`

All application-specific exceptions extend `BrightspotException`. This provides:
- Consistent error handling across the application
- Type-specific exception handling
- Clear exception hierarchy

**Location:** `com.brightspot.exception.BrightspotException`

---

## When to Use Which Exception

### `EncryptionException`

Use when:
- Encryption operations fail
- Decryption operations fail
- Invalid encryption parameters are provided
- Encryption keys are missing or invalid

**Example:**
```java
try {
    SecretKey secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
    Cipher cipher = Cipher.getInstance(TRANSFORM);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    // ...
} catch (GeneralSecurityException e) {
    throw new EncryptionException("Failed to encrypt data", e);
}
```

### `DatabaseOperationException`

Use when:
- Database connection fails
- SQL execution errors occur
- Transaction failures happen
- Data integrity violations occur
- Query execution fails

**Example:**
```java
try {
    return database.query(sql);
} catch (SQLException e) {
    throw new DatabaseOperationException("Failed to execute query", e);
}
```

### `ConfigurationException`

Use when:
- Required configuration properties are missing
- Configuration values are invalid
- Configuration files cannot be parsed
- Configuration settings are incompatible

**Example:**
```java
String apiKey = config.getProperty("api.key");
if (apiKey == null || apiKey.isEmpty()) {
    throw new ConfigurationException("API key is required but not configured");
}
```

### `ValidationException`

Use when:
- Input validation fails
- Business rules are violated
- Data format is invalid
- Constraints are violated

**Example:**
```java
if (email == null || !email.matches(EMAIL_PATTERN)) {
    throw new ValidationException("Invalid email format: " + email);
}
```

---

## Exception Handling Patterns

### Pattern 1: Catch Specific Exceptions

**✅ DO:**
```java
try {
    processPayment(request);
} catch (ValidationException e) {
    // Handle validation errors
    logger.warn("Payment validation failed: {}", e.getMessage());
    return errorResponse(400, e.getMessage());
} catch (DatabaseOperationException e) {
    // Handle database errors
    logger.error("Database error during payment processing", e);
    return errorResponse(500, "Payment processing temporarily unavailable");
}
```

**❌ DON'T:**
```java
try {
    processPayment(request);
} catch (Exception e) {
    // Too generic - can't handle appropriately
    logger.error("Error", e);
}
```

### Pattern 2: Preserve Exception Context

**✅ DO:**
```java
try {
    encrypt(data);
} catch (GeneralSecurityException e) {
    throw new EncryptionException("Failed to encrypt data", e);
}
```

**❌ DON'T:**
```java
try {
    encrypt(data);
} catch (GeneralSecurityException e) {
    throw new EncryptionException("Failed to encrypt data");
    // Lost the original exception!
}
```

### Pattern 3: Avoid Silent Failures

**✅ DO:**
```java
public Customer getCustomer(String customerId) throws StripeOperationException {
    try {
        return Customer.retrieve(customerId);
    } catch (StripeException e) {
        throw new StripeOperationException("Failed to retrieve customer", e);
    }
}
```

**❌ DON'T:**
```java
public Customer getCustomer(String customerId) {
    try {
        return Customer.retrieve(customerId);
    } catch (StripeException e) {
        logger.error("Error", e);
    }
    return null; // Silent failure!
}
```

### Pattern 4: Use Optional for Nullable Results

**✅ DO:**
```java
public Optional<Customer> findCustomer(String customerId) {
    try {
        return Optional.ofNullable(Customer.retrieve(customerId));
    } catch (StripeException e) {
        logger.warn("Customer not found: {}", customerId);
        return Optional.empty();
    }
}
```

---

## Best Practices

### 1. Catch Specific Exceptions

Always catch the most specific exception type you can handle. This allows for:
- Appropriate error handling strategies
- Better error messages
- Proper recovery mechanisms

### 2. Preserve Exception Context

Always include the original exception as the cause when wrapping:
```java
throw new EncryptionException("Encryption failed", originalException);
```

### 3. Document Thrown Exceptions

Use JavaDoc to document exceptions that methods may throw:
```java
/**
 * Encrypts the given input string.
 * 
 * @param input the string to encrypt
 * @return the encrypted string
 * @throws EncryptionException if encryption fails
 * @throws IllegalArgumentException if input is null
 */
public String encrypt(String input) throws EncryptionException {
    // ...
}
```

### 4. Validate Input Parameters

Validate inputs early and throw `IllegalArgumentException` for invalid inputs:
```java
public String encrypt(String input) throws EncryptionException {
    if (input == null) {
        throw new IllegalArgumentException("Input cannot be null");
    }
    // ... encryption logic
}
```

### 5. Use Appropriate Exception Types

- **Checked exceptions** (`BrightspotException` and subclasses): For recoverable errors that callers should handle
- **Unchecked exceptions** (`IllegalArgumentException`, `IllegalStateException`): For programming errors

### 6. Log Appropriately

- **ERROR**: System errors, unexpected exceptions
- **WARN**: Recoverable errors, validation failures
- **DEBUG**: Detailed diagnostic information

See [LOGGING_STANDARDS.md](LOGGING_STANDARDS.md) for detailed logging guidelines.

### 7. Don't Swallow Exceptions

Never use empty catch blocks. Always log or re-throw:
```java
// ❌ DON'T
try {
    doSomething();
} catch (Exception e) {
    // Swallowed!
}

// ✅ DO
try {
    doSomething();
} catch (Exception e) {
    logger.error("Error doing something", e);
    throw new BrightspotException("Operation failed", e);
}
```

---

## Common Patterns

### Pattern: Retry Logic

```java
public Customer getCustomerWithRetry(String customerId, int maxRetries) 
        throws StripeOperationException {
    int attempts = 0;
    while (attempts < maxRetries) {
        try {
            return Customer.retrieve(customerId);
        } catch (StripeException e) {
            attempts++;
            if (attempts >= maxRetries) {
                throw new StripeOperationException("Failed after " + maxRetries + " attempts", e);
            }
            logger.warn("Retry attempt {} for customer {}", attempts, customerId);
            sleep(retryDelay);
        }
    }
    throw new StripeOperationException("Failed to retrieve customer");
}
```

### Pattern: Fallback Mechanism

```java
public String getConfigurationValue(String key) {
    try {
        return primaryConfig.get(key);
    } catch (ConfigurationException e) {
        logger.warn("Primary config failed, using fallback", e);
        return fallbackConfig.get(key);
    }
}
```

### Pattern: Validation Chain

```java
public void validatePaymentRequest(PaymentRequest request) throws ValidationException {
    if (request.getAmount() == null) {
        throw new ValidationException("Payment amount is required");
    }
    if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new ValidationException("Payment amount must be positive");
    }
    if (request.getCurrency() == null) {
        throw new ValidationException("Payment currency is required");
    }
    // ... more validations
}
```

---

## Anti-Patterns to Avoid

### ❌ Generic Exception Catching

```java
// DON'T
try {
    doSomething();
} catch (Exception e) {
    // Too broad
}
```

### ❌ Silent Failures

```java
// DON'T
try {
    return processData();
} catch (Exception e) {
    logger.error("Error", e);
}
return null; // Silent failure
```

### ❌ Losing Exception Context

```java
// DON'T
try {
    encrypt(data);
} catch (GeneralSecurityException e) {
    throw new EncryptionException("Encryption failed");
    // Lost the original exception!
}
```

### ❌ Using Exceptions for Control Flow

```java
// DON'T
try {
    return findItem(id);
} catch (NotFoundException e) {
    return createItem(id);
}
```

### ❌ Empty Catch Blocks

```java
// DON'T
try {
    doSomething();
} catch (Exception e) {
    // Ignored!
}
```

### ❌ Catching and Re-throwing Without Context

```java
// DON'T
try {
    doSomething();
} catch (Exception e) {
    throw new Exception("Error occurred");
    // Lost original exception and context
}
```

---

## Migration Guide

When refactoring existing code to use the new exception hierarchy:

1. **Identify generic exception catches**: Find `catch (Exception e)` blocks
2. **Determine appropriate exception type**: Choose the most specific exception
3. **Wrap underlying exceptions**: Preserve original exception as cause
4. **Update method signatures**: Replace `throws Exception` with specific types
5. **Update callers**: Handle new exception types appropriately
6. **Add logging**: Ensure exceptions are logged with appropriate context

---

## References

- [Exception Handling Optimization Guide](EXCEPTION_HANDLING_OPTIMIZATION.md)
- [Logging Standards](LOGGING_STANDARDS.md)
- Oracle Java Exception Handling Best Practices
- Effective Java (Joshua Bloch) - Items 69-77

