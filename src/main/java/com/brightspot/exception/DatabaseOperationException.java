package com.brightspot.exception;

/**
 * Exception thrown when database operations fail.
 * 
 * <p>This exception should be used for errors related to:
 * <ul>
 *   <li>Database connection failures</li>
 *   <li>SQL execution errors</li>
 *   <li>Transaction failures</li>
 *   <li>Data integrity violations</li>
 *   <li>Query execution errors</li>
 * </ul>
 * </p>
 * 
 * <p>Always preserve the original database exception as the cause when wrapping
 * underlying SQL exceptions or database-related errors.</p>
 * 
 * @see BrightspotException
 */
public class DatabaseOperationException extends BrightspotException {
    
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new DatabaseOperationException with the specified message.
     * 
     * @param message the detail message describing the database operation error
     */
    public DatabaseOperationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new DatabaseOperationException with the specified message and cause.
     * 
     * <p>This constructor should be used when wrapping underlying exceptions such as
     * SQLException, DataAccessException, etc.</p>
     * 
     * @param message the detail message describing the database operation error
     * @param cause the cause (typically a SQLException or related database exception)
     */
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

