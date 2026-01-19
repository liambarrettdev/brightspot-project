package com.brightspot.exception;

/**
 * Base exception class for all Brightspot application errors.
 *
 * <p>This exception serves as the root of the exception hierarchy for the Brightspot application.
 * All domain-specific exceptions should extend this class to enable consistent error handling throughout the
 * application.</p>
 *
 * <p>This is a checked exception, meaning callers must explicitly handle it. Use unchecked
 * exceptions (extending RuntimeException) for programming errors that should not be caught.</p>
 *
 * @see RuntimeException for unchecked exceptions (programming errors)
 */
public class BrightspotException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new BrightspotException with the specified message.
     *
     * @param message the detail message
     */
    public BrightspotException(String message) {
        super(message);
    }

    /**
     * Constructs a new BrightspotException with the specified message and cause.
     *
     * <p>This constructor preserves the original exception context, which is essential
     * for debugging and root cause analysis.</p>
     *
     * @param message the detail message
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public BrightspotException(String message, Throwable cause) {
        super(message, cause);
    }
}

