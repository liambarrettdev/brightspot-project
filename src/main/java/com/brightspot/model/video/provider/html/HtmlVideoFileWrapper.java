package com.brightspot.model.video.provider.html;

import java.util.Map;
import java.util.Optional;

import com.brightspot.tool.field.annotation.MimeTypes;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StorageItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Recordable.Embedded
public class HtmlVideoFileWrapper extends Record {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlVideoFileWrapper.class);

    @Recordable.Required
    @MimeTypes("+video/")
    private StorageItem file;

    private Integer width;

    private Integer height;

    @ToolUi.Note("Time is seconds")
    private Long duration;

    public StorageItem getFile() {
        return file;
    }

    public void setFile(StorageItem file) {
        this.file = file;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getMimeType() {
        return file != null ? file.getContentType() : null;
    }

    // -- Overrides -- //

    @Override
    public void beforeSave() {
        super.beforeSave();

        try {
            // Validate dimensions
            if (width != null && width <= 0) {
                String errorMsg = "Video width must be greater than 0, received: " + width;
                LOGGER.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }

            if (height != null && height <= 0) {
                String errorMsg = "Video height must be greater than 0, received: " + height;
                LOGGER.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }

            // Validate duration
            if (duration != null && duration <= 0) {
                String errorMsg = "Video duration must be greater than 0, received: " + duration;
                LOGGER.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }

            // Automatically extract metadata from the video file if not manually set
            extractMetadataFromFile();
        } catch (IllegalArgumentException e) {
            // Re-throw validation exceptions
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during video file validation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save video file: " + e.getMessage(), e);
        }
    }

    @Override
    public String getLabel() {
        String label = null;

        if (file != null && file.getContentType() != null) {
            Map<String, Object> metadata = file.getMetadata();
            if (metadata != null) {
                label = ObjectUtils.to(String.class, metadata.get("originalFilename"));
            }
            if (label == null) {
                label = file.getContentType();
            }
        }

        return label;
    }

    // -- Helper Methods -- //

    /**
     * Automatically extracts video metadata (width, height, duration) from the StorageItem if these values are not
     * already set manually.
     */
    private void extractMetadataFromFile() {
        if (file == null) {
            LOGGER.debug("No video file present, skipping metadata extraction");
            return;
        }

        try {
            Map<String, Object> metadata = file.getMetadata();
            if (metadata == null || metadata.isEmpty()) {
                LOGGER.debug("No metadata available for video file");
                return;
            }

            // Extract width if not manually set
            if (width == null) {
                width = Optional.ofNullable(metadata.get("width"))
                    .map(w -> ObjectUtils.to(Integer.class, w))
                    .filter(w -> w > 0)
                    .orElse(null);

                if (width != null) {
                    LOGGER.debug("Extracted video width from metadata: {}", width);
                }
            }

            // Extract height if not manually set
            if (height == null) {
                height = Optional.ofNullable(metadata.get("height"))
                    .map(h -> ObjectUtils.to(Integer.class, h))
                    .filter(h -> h > 0)
                    .orElse(null);

                if (height != null) {
                    LOGGER.debug("Extracted video height from metadata: {}", height);
                }
            }

            // Extract duration if not manually set
            if (duration == null) {
                duration = Optional.ofNullable(metadata.get("duration"))
                    .map(d -> ObjectUtils.to(Long.class, d))
                    .filter(d -> d > 0)
                    .orElse(null);

                if (duration != null) {
                    LOGGER.debug("Extracted video duration from metadata: {} seconds", duration);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to extract metadata from video file: {}", e.getMessage(), e);
            // Don't throw - metadata extraction is optional and shouldn't prevent save
        }
    }
}
