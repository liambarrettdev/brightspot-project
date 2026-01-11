package com.brightspot.utils;

/**
 * Utility class for common validation operations.
 * <p>
 * This class provides static methods for validating objects, strings, and other common types. All methods throw
 * {@link IllegalArgumentException} when validation fails.
 */
public final class ValidationUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ValidationUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Validates that none of the provided values are null.
     *
     * @param values array of value-name pairs: [value1, "name1", value2, "name2", ...]
     * @throws IllegalArgumentException if any value is null
     */
    public static void requireNonNull(Object... values) {
        if (values == null || values.length == 0) {
            return;
        }

        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("Values array must contain value-name pairs");
        }

        for (int i = 0; i < values.length; i += 2) {
            Object value = values[i];
            String name = (String) values[i + 1];
            if (value == null) {
                throw new IllegalArgumentException(name + " cannot be null");
            }
        }
    }
}
