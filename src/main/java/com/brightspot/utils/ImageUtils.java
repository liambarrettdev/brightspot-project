package com.brightspot.utils;

import java.util.Optional;

import com.brightspot.model.image.Image;

/**
 * Utility class for working with image metadata.
 *
 * <p>This class provides methods to extract metadata values from Image objects,
 * with support for fallback values when metadata is missing.</p>
 */
public final class ImageUtils {

    private ImageUtils() {
    }

    /**
     * Gets a metadata value from an image.
     *
     * <p>Returns {@code null} if the image is null, the metadata is null, or
     * the key is not found in the metadata.</p>
     *
     * @param image the image to get metadata from
     * @param key the metadata key
     * @return the metadata value as a string, or {@code null} if not found
     */
    public static String getImageMetadataValue(Image image, String key) {
        return getImageMetadataValue(image, key, null);
    }

    /**
     * Gets a metadata value from an image with a fallback.
     *
     * <p>Returns the metadata value for the given key, or the fallback value if
     * the key is not found. Returns {@code null} if the image is null or the
     * metadata is null.</p>
     *
     * @param image the image to get metadata from
     * @param key the metadata key
     * @param fallback the fallback value if key is not found
     * @return the metadata value as a string, the fallback, or {@code null} if image/metadata is null
     */
    public static String getImageMetadataValue(Image image, String key, String fallback) {
        return Optional.ofNullable(image)
            .map(Image::getImageMetadata)
            .map(metadata -> metadata.getOrDefault(key, fallback))
            .map(String::valueOf)
            .orElse(null);
    }
}
