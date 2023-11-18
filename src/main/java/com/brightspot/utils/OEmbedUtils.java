package com.brightspot.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import com.psddev.dari.util.CollectionUtils;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StorageItem;
import org.apache.commons.lang3.StringUtils;

/**
 * Fields available in all OEmbed responses.
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

    public static String getTitle(Object metadata) {
        return getValue(String.class, metadata, TITLE);
    }

    public static String getDescription(Object metadata) {
        return getValue(String.class, metadata, DESCRIPTION);
    }

    public static Integer getDuration(Object metadata) {
        return getValue(Integer.class, metadata, DURATION);
    }

    public static Integer getHeight(Object metadata) {
        return getValue(Integer.class, metadata, HEIGHT);
    }

    public static Integer getWidth(Object metadata) {
        return getValue(Integer.class, metadata, WIDTH);
    }

    public static StorageItem getThumbnail(Object metadata) {
        String thumbnailUrl = getValue(String.class, metadata, THUMBNAIL_URL);
        if (StringUtils.isNotBlank(thumbnailUrl)) {
            StorageItem thumbnail = StorageItem.Static.createUrl(thumbnailUrl);

            Map<String, Object> imageMetadata = new LinkedHashMap<>();
            imageMetadata.put("width", getValue(Integer.class, metadata, THUMBNAIL_WIDTH));
            imageMetadata.put("height", getValue(Integer.class, metadata, THUMBNAIL_HEIGHT));
            imageMetadata.put("cms.edits", new LinkedHashMap<>());
            imageMetadata.put("cms.crops", new LinkedHashMap<>());
            imageMetadata.put("cms.focus", new LinkedHashMap<>());

            thumbnail.setMetadata(imageMetadata);

            return thumbnail;
        }

        return null;
    }

    private static <T> T getValue(Class<T> type, Object data, String key) {
        if (ObjectUtils.isBlank(data)) {
            return null;
        }

        return ObjectUtils.to(type, CollectionUtils.getByPath(data, key));
    }
}
