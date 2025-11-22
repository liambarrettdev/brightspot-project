package com.brightspot.exception;

/**
 * Exception thrown when configuration errors occur.
 * 
 * <p>This exception should be used for errors related to:
 * <ul>
 *   <li>Missing required configuration properties</li>
 *   <li>Invalid configuration values</li>
 *   <li>Configuration file parsing errors</li>
 *   <li>Incompatible configuration settings</li>
 * </ul>
 * </p>
 * 
 * <p>Configuration errors are typically non-recoverable and should be caught early
 * during application startup or initialization.</p>
 * 
 * @see BrightspotException
 */
public class ConfigurationException extends BrightspotException {
    
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ConfigurationException with the specified message.
     * 
     * <p>Configuration exceptions typically don't have an underlying cause, as they
     * represent configuration problems rather than runtime failures.</p>
     * 
     * @param message the detail message describing the configuration error
     */
    public ConfigurationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ConfigurationException with the specified message and cause.
     * 
     * <p>This constructor should be used when a configuration error is caused by
     * an underlying exception (e.g., IOException when reading a config file).</p>
     * 
     * @param message the detail message describing the configuration error
     * @param cause the cause (e.g., IOException when reading config file)
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

