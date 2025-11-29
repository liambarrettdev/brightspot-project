package com.brightspot.exception;

/**
 * Exception thrown when encryption or decryption operations fail.
 *
 * <p>This exception should be used for errors related to:
 * <ul>
 *   <li>Encryption failures (e.g., GeneralSecurityException)</li>
 *   <li>Decryption failures</li>
 *   <li>Invalid encryption parameters</li>
 *   <li>Missing or invalid encryption keys</li>
 * </ul>
 * </p>
 *
 * <p>Always preserve the original exception as the cause when wrapping underlying
 * security exceptions.</p>
 *
 * @see BrightspotException
 */
public class EncryptionException extends BrightspotException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new EncryptionException with the specified message.
     *
     * @param message the detail message describing the encryption error
     */
    public EncryptionException(String message) {
        super(message);
    }

    /**
     * Constructs a new EncryptionException with the specified message and cause.
     *
     * <p>This constructor should be used when wrapping underlying exceptions such as
     * GeneralSecurityException, InvalidKeyException, etc.</p>
     *
     * @param message the detail message describing the encryption error
     * @param cause the cause (typically a GeneralSecurityException or related exception)
     */
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}

