package com.brightspot.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import com.psddev.dari.util.CollectionUtils;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StorageItem;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for working with OEmbed metadata.
 *
 * <p>This class provides methods to extract OEmbed response fields from metadata objects.
 * OEmbed is a format for embedding content from third-party sites (e.g., YouTube, Twitter).</p>
 *
 * <p>Fields available in all OEmbed responses are defined as constants. The class supports
 * extracting common fields like title, description, dimensions, and thumbnail information.</p>
 *
 * @see <a href="http://oembed.com/">http://oembed.com/</a>
 */
public final class OEmbedUtils {

    public static final String TYPE = "type"; // Required: photo, video, link or rich
    public static final String VERSION = "version";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String DURATION = "duration";
    public static final String AUTHOR_NAME = "author_name";
    public static final String AUTHOR_URL = "author_url";
    public static final String PROVIDER_NAME = "provider_name";
    public static final String PROVIDER_URL = "provider_url";
    public static final String CACHE_AGE = "cache_age";
    public static final String THUMBNAIL_URL = "thumbnail_url";
    public static final String THUMBNAIL_HEIGHT = "thumbnail_height";
    public static final String THUMBNAIL_WIDTH = "thumbnail_width";

    // --- (type == 'photo')
    public static final String URL = "url";

    // --- (type == 'photo', 'video', 'rich')
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";

    // --- (type == 'video', 'rich')
    public static final String HTML = "html";

    private OEmbedUtils() {
    }

    // -- Statics -- //

    /**
     * Extracts the title from OEmbed metadata.
     *
     * @param metadata the OEmbed metadata object
     * @return the title string, or {@code null} if not found
     */
    public static String getTitle(Object metadata) {
        return getValue(String.class, metadata, TITLE);
    }

    /**
     * Extracts the description from OEmbed metadata.
     *
     * @param metadata the OEmbed metadata object
     * @return the description string, or {@code null} if not found
     */
    public static String getDescription(Object metadata) {
        return getValue(String.class, metadata, DESCRIPTION);
    }

    /**
     * Extracts the duration from OEmbed metadata.
     *
     * <p>Duration is typically in seconds and is commonly used for video content.</p>
     *
     * @param metadata the OEmbed metadata object
     * @return the duration as an Integer, or {@code null} if not found
     */
    public static Integer getDuration(Object metadata) {
        return getValue(Integer.class, metadata, DURATION);
    }

    /**
     * Extracts the height from OEmbed metadata.
     *
     * <p>Height is typically in pixels and is used for photo, video, and rich content types.</p>
     *
     * @param metadata the OEmbed metadata object
     * @return the height as an Integer, or {@code null} if not found
     */
    public static Integer getHeight(Object metadata) {
        return getValue(Integer.class, metadata, HEIGHT);
    }

    /**
     * Extracts the width from OEmbed metadata.
     *
     * <p>Width is typically in pixels and is used for photo, video, and rich content types.</p>
     *
     * @param metadata the OEmbed metadata object
     * @return the width as an Integer, or {@code null} if not found
     */
    public static Integer getWidth(Object metadata) {
        return getValue(Integer.class, metadata, WIDTH);
    }

    /**
     * Creates a StorageItem from the thumbnail URL in OEmbed metadata.
     *
     * <p>Extracts the thumbnail URL and dimensions from the metadata and creates a
     * StorageItem with appropriate metadata. Returns {@code null} if no thumbnail URL
     * is found in the metadata.</p>
     *
     * <p>The created StorageItem includes width, height, and empty edit/crop/focus
     * metadata maps for compatibility with the image system.</p>
     *
     * @param metadata the OEmbed metadata object
     * @return a StorageItem representing the thumbnail, or {@code null} if no thumbnail URL
     */
    public static StorageItem getThumbnail(Object metadata) {
        String thumbnailUrl = getValue(String.class, metadata, THUMBNAIL_URL);
        if (StringUtils.isNotBlank(thumbnailUrl)) {
            Map<String, Object> imageMetadata = new LinkedHashMap<>();
            imageMetadata.put("width", getValue(Integer.class, metadata, THUMBNAIL_WIDTH));
            imageMetadata.put("height", getValue(Integer.class, metadata, THUMBNAIL_HEIGHT));
            imageMetadata.put("cms.edits", new LinkedHashMap<>());
            imageMetadata.put("cms.crops", new LinkedHashMap<>());
            imageMetadata.put("cms.focus", new LinkedHashMap<>());

            StorageItem thumbnail = StorageItem.Static.createUrl(thumbnailUrl);

            thumbnail.setMetadata(imageMetadata);

            return thumbnail;
        }
        return null;
    }

    /**
     * Extracts a typed value from a nested data structure using a path key.
     *
     * <p>Uses CollectionUtils.getByPath to navigate nested maps/lists and converts
     * the result to the requested type. Returns {@code null} if the data is blank
     * or the key path is not found.</p>
     *
     * @param <T> the type to convert to
     * @param type the target class
     * @param data the data structure to extract from
     * @param key the path key (supports dot notation for nested paths)
     * @return the extracted and converted value, or {@code null} if not found
     */
    private static <T> T getValue(Class<T> type, Object data, String key) {
        if (ObjectUtils.isBlank(data)) {
            return null;
        }
        return ObjectUtils.to(type, CollectionUtils.getByPath(data, key));
    }
}
