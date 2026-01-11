package com.brightspot.exception;

/**
 * Exception thrown when migration operations fail.
 *
 * <p>This exception is used to indicate errors that occur during data migration processes,
 * such as when migrating data from legacy systems to the current application. Common scenarios
 * include:</p>
 *
 * <ul>
 *   <li>Failure to instantiate migration classes</li>
 *   <li>Errors during legacy data parsing or transformation</li>
 *   <li>Issues with legacy record lookup or creation</li>
 *   <li>Problems connecting to or querying legacy data sources</li>
 * </ul>
 *
 * <p>This is a checked exception, meaning callers must explicitly handle it. When throwing
 * this exception, always include a descriptive message and preserve the original exception
 * as the cause when available.</p>
 *
 * @see BrightspotException
 */
public class MigrationException extends BrightspotException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new MigrationException with the specified message.
     *
     * @param message the detail message describing the migration error
     */
    public MigrationException(String message) {
        super(message);
    }

    /**
     * Constructs a new MigrationException with the specified message and cause.
     *
     * <p>This constructor preserves the original exception context, which is essential
     * for debugging and root cause analysis during migration operations.</p>
     *
     * @param message the detail message describing the migration error
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public MigrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
