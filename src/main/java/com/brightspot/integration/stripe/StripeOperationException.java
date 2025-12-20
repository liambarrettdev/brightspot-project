package com.brightspot.integration.stripe;

import com.brightspot.exception.BrightspotException;

/**
 * Exception thrown when Stripe API operations fail.
 *
 * <p>This exception should be used for errors related to:
 * <ul>
 *   <li>Stripe API call failures</li>
 *   <li>Payment processing errors</li>
 *   <li>Customer management errors</li>
 *   <li>Payment method operations</li>
 * </ul>
 * </p>
 *
 * <p>Always preserve the original StripeException as the cause when wrapping
 * Stripe API exceptions.</p>
 *
 * @see BrightspotException
 */
public class StripeOperationException extends BrightspotException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new StripeOperationException with the specified message.
     *
     * @param message the detail message describing the Stripe operation error
     */
    public StripeOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new StripeOperationException with the specified message and cause.
     *
     * <p>This constructor should be used when wrapping StripeException instances
     * to preserve the original error context.</p>
     *
     * @param message the detail message describing the Stripe operation error
     * @param cause the cause (typically a StripeException)
     */
    public StripeOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
