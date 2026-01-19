package com.brightspot.auth;

import javax.servlet.ServletException;

/**
 * Exception thrown when authentication fails.
 *
 * <p>This exception extends {@link ServletException} for servlet API compatibility.</p>
 *
 * @see ServletException
 */
public class AuthenticationException extends ServletException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new AuthenticationException with the specified message.
     *
     * @param message the detail message describing the authentication error
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs a new AuthenticationException with the specified message and cause.
     *
     * @param message the detail message describing the authentication error
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
