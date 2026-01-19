package com.brightspot.report;

import javax.servlet.ServletException;

/**
 * Exception thrown when report execution fails.
 *
 * <p>This exception should be used for errors that occur during report generation,
 * such as data retrieval failures, formatting errors, or export failures.</p>
 *
 * @see ServletException
 */
public class ReportExecutionException extends ServletException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ReportExecutionException with the specified message.
     *
     * @param message the detail message describing the report execution error
     */
    public ReportExecutionException(String message) {
        super(message);
    }

    /**
     * Constructs a new ReportExecutionException with the specified message and cause.
     *
     * <p>This constructor should be used when report execution fails due to an
     * underlying exception.</p>
     *
     * @param message the detail message describing the report execution error
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public ReportExecutionException(String message, Exception cause) {
        super(message, cause);
    }
}
