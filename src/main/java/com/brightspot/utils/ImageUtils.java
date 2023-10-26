package com.brightspot.utils;

import java.util.Optional;

import com.brightspot.model.image.Image;

public final class ImageUtils {

    private ImageUtils() {
    }

    public static String getImageMetadataValue(Image image, String key) {
        return getImageMetadataValue(image, key, null);
    }

    public static String getImageMetadataValue(Image image, String key, String fallback) {
        return Optional.ofNullable(image)
            .map(Image::getImageMetadata)
            .map(metadata -> metadata.getOrDefault(key, fallback))
            .map(String::valueOf)
            .orElse(null);
    }
}
