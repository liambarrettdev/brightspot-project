package com.brightspot.exception;

/**
 * Exception thrown when validation fails.
 * 
 * <p>This exception should be used for errors related to:
 * <ul>
 *   <li>Input validation failures</li>
 *   <li>Business rule violations</li>
 *   <li>Data format validation errors</li>
 *   <li>Constraint violations</li>
 * </ul>
 * </p>
 * 
 * <p>Validation exceptions are typically recoverable and should result in user-friendly
 * error messages. They are often caught at the servlet/API layer and returned as
 * HTTP 400 (Bad Request) responses.</p>
 * 
 * @see BrightspotException
 */
public class ValidationException extends BrightspotException {
    
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ValidationException with the specified message.
     * 
     * <p>The message should be user-friendly and describe what validation failed.
     * This message may be displayed directly to end users.</p>
     * 
     * @param message the detail message describing the validation error
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ValidationException with the specified message and cause.
     * 
     * <p>This constructor should be used when validation fails due to an underlying
     * exception (e.g., when parsing a date format fails).</p>
     * 
     * @param message the detail message describing the validation error
     * @param cause the cause (e.g., ParseException when validating date format)
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

