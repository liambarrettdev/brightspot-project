package com.brightspot.model.video.provider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import com.brightspot.utils.OEmbedUtils;
import com.psddev.cms.db.ExternalContent;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StorageItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExternalVideoSource extends VideoSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalVideoSource.class);

    protected abstract String getVideoUrlFormat();

    @Recordable.Required
    @DisplayName("Video ID or URL")
    private String videoId;

    @Recordable.Embedded
    @ToolUi.Hidden
    private ExternalContent externalContent;

    // Caching fields to avoid unnecessary API calls
    private transient String cachedVideoUrl;
    private transient boolean metadataInitialized;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    // -- Overrides -- //

    @Override
    public String getLabel() {
        return ObjectUtils.firstNonBlank(OEmbedUtils.getTitle(getExternalMetadata()), getVideoId());
    }

    @Override
    public void beforeSave() {
        super.beforeSave();

        updateExternalContent();
    }

    @Override
    public String getVideoTitleFallback() {
        return OEmbedUtils.getTitle(getExternalMetadata());
    }

    @Override
    public String getVideoDescriptionFallback() {
        return OEmbedUtils.getDescription(getExternalMetadata());
    }

    @Override
    public StorageItem getVideoThumbnailFallback() {
        return OEmbedUtils.getThumbnail(getExternalMetadata());
    }

    @Override
    public Integer getVideoHeight() {
        return OEmbedUtils.getHeight(getExternalMetadata());
    }

    @Override
    public Integer getVideoWidth() {
        return OEmbedUtils.getWidth(getExternalMetadata());
    }

    // -- Helper Methods -- //

    public String getVideoUrl() {
        String videoUrl = null;
        String identifier = getVideoId();

        if (!StringUtils.isBlank(identifier)) {
            identifier = identifier.trim();

            URL url = null;
            try {
                url = new URL(identifier);
            } catch (MalformedURLException e) {
                // Identifier is not a valid URL, treating it as a video ID
                LOGGER.debug("Identifier '{}' is not a valid URL, treating as video ID: {}",
                    identifier, e.getMessage());
            }

            if (url != null) {
                videoUrl = identifier;
                LOGGER.debug("Using full URL for video: {}", videoUrl);
            } else {
                videoUrl = String.format(getVideoUrlFormat(), identifier);
                LOGGER.debug("Generated video URL from ID '{}': {}", identifier, videoUrl);
            }
        }
        return videoUrl;
    }

    protected Object getExternalMetadata() {
        updateExternalContent();

        return Optional.ofNullable(externalContent)
            .map(ExternalContent::getState)
            .map(state -> state.get("response"))
            .orElse(null);
    }

    // -- Utility Methods -- //

    /**
     * Updates the external content metadata only if the video URL has changed or if metadata has not been initialized
     * yet. This improves performance by caching external API calls.
     */
    private void updateExternalContent() {
        String currentVideoUrl = getVideoUrl();

        // Check if we need to update based on URL change or initialization status
        boolean urlChanged = !ObjectUtils.equals(cachedVideoUrl, currentVideoUrl);

        if (metadataInitialized && !urlChanged) {
            // Already initialized with the same URL, no update needed
            return;
        }

        // Update external content
        if (currentVideoUrl != null) {
            // Only create new ExternalContent if URL changed or first initialization
            if (externalContent == null || urlChanged) {
                try {
                    externalContent = new ExternalContent();
                    externalContent.setUrl(currentVideoUrl);
                    Object response = externalContent.getResponse();

                    if (response == null) {
                        LOGGER.warn("No response received from external video provider for URL: {}", currentVideoUrl);
                    } else {
                        LOGGER.debug("Successfully fetched external metadata for URL: {}", currentVideoUrl);
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to fetch external content for video URL '{}': {}",
                        currentVideoUrl, e.getMessage(), e);
                    externalContent = null;
                }
            }
        } else {
            if (videoId != null && !videoId.trim().isEmpty()) {
                LOGGER.warn("Unable to generate video URL from identifier: {}", videoId);
            }
            externalContent = null;
        }

        // Update cache state
        cachedVideoUrl = currentVideoUrl;
        metadataInitialized = true;
    }
}
