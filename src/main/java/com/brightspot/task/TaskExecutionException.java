package com.brightspot.task;

import javax.servlet.ServletException;

/**
 * Exception thrown when task execution fails.
 *
 * <p>This exception should be used for errors that occur during task execution,
 * such as processing errors, data validation failures, or external service
 * communication failures.</p>
 *
 * @see ServletException
 */
public class TaskExecutionException extends ServletException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new TaskExecutionException with the specified message.
     *
     * @param message the detail message describing the task execution error
     */
    public TaskExecutionException(String message) {
        super(message);
    }

    /**
     * Constructs a new TaskExecutionException with the specified message and cause.
     *
     * <p>This constructor should be used when task execution fails due to an
     * underlying exception.</p>
     *
     * @param message the detail message describing the task execution error
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
