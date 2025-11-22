# Logging Standards

This document defines the logging standards for the Brightspot codebase. Consistent logging is essential for debugging, monitoring, and maintaining the application.

## Table of Contents

1. [Logging Framework](#logging-framework)
2. [Logger Initialization](#logger-initialization)
3. [Log Levels](#log-levels)
4. [Logging Patterns](#logging-patterns)
5. [Exception Logging](#exception-logging)
6. [Context Information](#context-information)
7. [Performance Considerations](#performance-considerations)
8. [Best Practices](#best-practices)

---

## Logging Framework

The Brightspot application uses **SLF4J** (Simple Logging Facade for Java) as the logging abstraction layer.

**Import statements:**
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
```

**Why SLF4J?**
- Provides a simple abstraction over various logging frameworks
- Supports parameterized logging (better performance)
- Allows switching underlying logging implementations without code changes

---

## Logger Initialization

### Static Logger (Recommended)

For most classes, use a static logger:

```java
public class MyClass {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);
    
    public void doSomething() {
        LOGGER.info("Doing something");
    }
}
```

### Abstract Logger Method

For abstract classes or when logger needs to be overridden:

```java
public abstract class AbstractTask {
    protected abstract Logger logger();
    
    protected void execute() {
        logger().info("Executing task");
    }
}
```

**Implementation:**
```java
public class ConcreteTask extends AbstractTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcreteTask.class);
    
    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
```

---

## Log Levels

Use log levels consistently based on the severity and nature of the event:

### ERROR

**When to use:**
- System errors that require immediate attention
- Unexpected exceptions that indicate bugs or system failures
- Critical failures that prevent normal operation
- Errors that cannot be recovered from automatically

**Examples:**
```java
LOGGER.error("Failed to process payment for request {}", requestId, exception);
LOGGER.error("Database connection failed", exception);
LOGGER.error("Critical configuration missing: {}", configKey);
```

### WARN

**When to use:**
- Recoverable errors that don't prevent operation
- Validation failures
- Deprecated feature usage
- Performance issues that don't cause failures
- Retryable failures

**Examples:**
```java
LOGGER.warn("Payment validation failed for request {}: {}", requestId, validationError);
LOGGER.warn("Retrying operation after failure: {}", operationName);
LOGGER.warn("Using deprecated method: {}", methodName);
```

### INFO

**When to use:**
- Important business events
- Application lifecycle events (startup, shutdown)
- Significant state changes
- Key operations that succeed

**Examples:**
```java
LOGGER.info("Application started successfully");
LOGGER.info("Payment processed successfully for request {}", requestId);
LOGGER.info("User {} logged in", userId);
```

### DEBUG

**When to use:**
- Detailed diagnostic information
- Method entry/exit (for complex methods)
- Variable values during debugging
- Flow control information

**Examples:**
```java
LOGGER.debug("Processing payment request: amount={}, currency={}", amount, currency);
LOGGER.debug("Entering method: processPayment()");
LOGGER.debug("Retrieved customer: {}", customerId);
```

### TRACE

**When to use:**
- Very detailed diagnostic information
- Fine-grained execution flow
- Detailed variable dumps
- Only use when DEBUG is insufficient

**Examples:**
```java
LOGGER.trace("Step 1: Validating input parameters");
LOGGER.trace("Step 2: Checking database connection");
```

---

## Logging Patterns

### Pattern 1: Parameterized Logging

**✅ DO: Use parameterized logging for better performance**

```java
LOGGER.info("Processing payment for request {} with amount {}", requestId, amount);
LOGGER.error("Failed to retrieve customer {}: {}", customerId, exception.getMessage(), exception);
```

**❌ DON'T: String concatenation (inefficient)**

```java
LOGGER.info("Processing payment for request " + requestId + " with amount " + amount);
LOGGER.error("Failed to retrieve customer " + customerId + ": " + exception.getMessage());
```

**Why?**
- Parameterized logging avoids string concatenation when the log level is disabled
- Better performance, especially for DEBUG/TRACE logs
- Cleaner code

### Pattern 2: Exception Logging

**✅ DO: Always include the exception as the last parameter**

```java
try {
    processPayment(request);
} catch (PaymentException e) {
    LOGGER.error("Payment processing failed for request {}", requestId, e);
    throw e;
}
```

**❌ DON'T: Log exception message only**

```java
catch (PaymentException e) {
    LOGGER.error("Payment processing failed: " + e.getMessage()); // Lost stack trace!
}
```

### Pattern 3: Conditional Logging

**✅ DO: Use SLF4J's conditional methods for expensive operations**

```java
if (LOGGER.isDebugEnabled()) {
    LOGGER.debug("Complex object: {}", expensiveToStringOperation());
}
```

**For simple cases, parameterized logging is sufficient:**
```java
// This is fine - toString() only called if DEBUG is enabled
LOGGER.debug("Customer: {}", customer);
```

---

## Exception Logging

### Standard Exception Logging Pattern

Always log exceptions with:
1. Descriptive message with context
2. Exception object (for stack trace)
3. Appropriate log level

```java
try {
    encrypt(data);
} catch (EncryptionException e) {
    LOGGER.error("Encryption failed for data length: {}", data.length(), e);
    throw e;
}
```

### Exception Logging by Type

#### ValidationException → WARN

```java
try {
    validatePayment(request);
} catch (ValidationException e) {
    LOGGER.warn("Payment validation failed for request {}: {}", requestId, e.getMessage(), e);
    // Handle validation error
}
```

#### DatabaseOperationException → ERROR

```java
try {
    saveToDatabase(entity);
} catch (DatabaseOperationException e) {
    LOGGER.error("Database operation failed for entity {}", entity.getId(), e);
    throw e;
}
```

#### ConfigurationException → ERROR

```java
try {
    loadConfiguration();
} catch (ConfigurationException e) {
    LOGGER.error("Configuration error: {}", e.getMessage(), e);
    throw e;
}
```

#### EncryptionException → ERROR

```java
try {
    encrypt(data);
} catch (EncryptionException e) {
    LOGGER.error("Encryption failed", e);
    throw e;
}
```

#### Unexpected Exceptions → ERROR

```java
try {
    processRequest(request);
} catch (Exception e) {
    LOGGER.error("Unexpected error processing request {}", requestId, e);
    throw new BrightspotException("Request processing failed", e);
}
```

---

## Context Information

Always include relevant context in log messages to aid debugging:

### Request Context

```java
LOGGER.info("Processing request {} from user {}", requestId, userId);
LOGGER.error("Request {} failed: {}", requestId, errorMessage, exception);
```

### Operation Context

```java
LOGGER.info("Starting payment processing for order {}", orderId);
LOGGER.debug("Encrypting data of length {} using algorithm {}", data.length(), algorithm);
```

### State Context

```java
LOGGER.warn("Retry attempt {} of {} for operation {}", attempt, maxRetries, operationName);
LOGGER.info("Task {} completed in {} ms", taskName, duration);
```

### User Context

```java
LOGGER.info("User {} performed action {}", userId, action);
LOGGER.warn("Unauthorized access attempt by user {}", userId);
```

---

## Performance Considerations

### 1. Use Parameterized Logging

Parameterized logging avoids string concatenation when log level is disabled:

```java
// ✅ Good - no string concatenation if DEBUG is disabled
LOGGER.debug("Processing customer {} with {} items", customerId, itemCount);

// ❌ Bad - always performs string concatenation
LOGGER.debug("Processing customer " + customerId + " with " + itemCount + " items");
```

### 2. Guard Expensive Operations

For expensive operations (e.g., calling toString() on large objects), guard with level check:

```java
if (LOGGER.isDebugEnabled()) {
    LOGGER.debug("Complex object state: {}", expensiveToStringOperation());
}
```

### 3. Avoid Logging in Tight Loops

```java
// ❌ Bad - logs thousands of times
for (Item item : items) {
    LOGGER.debug("Processing item {}", item.getId());
    process(item);
}

// ✅ Good - log summary
LOGGER.debug("Processing {} items", items.size());
for (Item item : items) {
    process(item);
}
LOGGER.debug("Completed processing {} items", items.size());
```

### 4. Don't Log Sensitive Information

**Never log:**
- Passwords or password hashes
- Credit card numbers
- Social security numbers
- API keys or secrets
- Encryption keys

```java
// ❌ Bad
LOGGER.debug("User password: {}", password);
LOGGER.info("Credit card number: {}", creditCardNumber);

// ✅ Good
LOGGER.debug("User attempting login: {}", username);
LOGGER.info("Payment processed for card ending in {}", lastFourDigits);
```

---

## Best Practices

### ✅ DO

1. **Use appropriate log levels** - ERROR for system errors, WARN for recoverable issues, INFO for important events, DEBUG for diagnostics

2. **Include context** - Always include relevant IDs, operation names, and state information

3. **Log exceptions properly** - Always pass exception as last parameter to preserve stack trace

4. **Use parameterized logging** - Better performance and cleaner code

5. **Log at method boundaries** - Log entry/exit for important methods (at DEBUG level)

6. **Log state changes** - Log important state transitions and business events

7. **Use consistent format** - Follow a consistent message format across the codebase

### ❌ DON'T

1. **Don't log sensitive information** - Never log passwords, credit cards, API keys, etc.

2. **Don't use string concatenation** - Use parameterized logging instead

3. **Don't log too frequently** - Avoid logging in tight loops or high-frequency operations

4. **Don't log without context** - Always include relevant IDs and operation details

5. **Don't ignore exceptions** - Always log exceptions, even if re-throwing

6. **Don't use System.out.println** - Always use the logger

7. **Don't log at wrong levels** - Don't use ERROR for warnings or INFO for debug information

---

## Log Message Format

Use a consistent format for log messages:

```
[Level] [Context] Message with parameters: {param1}, {param2}
```

**Examples:**
```
INFO  Processing payment for request abc123 with amount 100.00
ERROR Database operation failed for entity xyz789: Connection timeout
WARN  Payment validation failed for request def456: Invalid amount
DEBUG Entering method: processPayment() with 3 parameters
```

---

## Structured Logging (Future Enhancement)

Consider implementing structured logging for better log analysis:

```java
// Future: Structured logging with key-value pairs
LOGGER.info("Payment processed", 
    kv("requestId", requestId),
    kv("amount", amount),
    kv("currency", currency),
    kv("duration", duration));
```

This enables:
- Better log aggregation and analysis
- Easier filtering and searching
- Integration with log analysis tools (ELK, Splunk, etc.)

---

## Examples

### Example 1: Method with Exception Handling

```java
public Customer getCustomer(String customerId) throws StripeOperationException {
    LOGGER.debug("Retrieving customer: {}", customerId);
    
    try {
        Customer customer = Customer.retrieve(customerId);
        LOGGER.debug("Successfully retrieved customer: {}", customerId);
        return customer;
    } catch (StripeException e) {
        LOGGER.error("Failed to retrieve customer {}: {}", customerId, e.getMessage(), e);
        throw new StripeOperationException("Failed to retrieve customer", e);
    }
}
```

### Example 2: Validation with Logging

```java
public void validatePayment(PaymentRequest request) throws ValidationException {
    LOGGER.debug("Validating payment request: {}", request.getId());
    
    if (request.getAmount() == null) {
        LOGGER.warn("Payment validation failed: amount is null for request {}", request.getId());
        throw new ValidationException("Payment amount is required");
    }
    
    if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        LOGGER.warn("Payment validation failed: invalid amount {} for request {}", 
            request.getAmount(), request.getId());
        throw new ValidationException("Payment amount must be positive");
    }
    
    LOGGER.debug("Payment request validated successfully: {}", request.getId());
}
```

### Example 3: Task Execution with Logging

```java
@Override
protected void execute() {
    LOGGER.info("Starting task: {}", getClass().getSimpleName());
    long startTime = System.currentTimeMillis();
    
    try {
        doWork();
        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info("Task completed successfully in {} ms", duration);
    } catch (Exception e) {
        long duration = System.currentTimeMillis() - startTime;
        LOGGER.error("Task failed after {} ms", duration, e);
        throw e;
    }
}
```

---

## References

- [Exception Handling Guidelines](EXCEPTION_HANDLING_GUIDELINES.md)
- [Exception Handling Optimization Guide](EXCEPTION_HANDLING_OPTIMIZATION.md)
- SLF4J Documentation: http://www.slf4j.org/manual.html
- SLF4J FAQ: http://www.slf4j.org/faq.html

