package com.brightspot.model.video.provider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import com.brightspot.utils.OEmbedUtils;
import com.psddev.cms.db.ExternalContent;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StorageItem;
import org.apache.commons.lang3.StringUtils;

public abstract class ExternalVideoSource extends VideoSource {

    protected abstract String getVideoUrlFormat();

    @Required
    @DisplayName("Video ID or URL")
    private String videoId;

    @Embedded
    @ToolUi.Hidden
    private ExternalContent externalContent;

    private transient boolean noUpdateRequired;

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
                // ignore and assume it's already an ID.
            }

            if (url != null) {
                videoUrl = identifier;
            } else {
                videoUrl = String.format(getVideoUrlFormat(), identifier);
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

    private void updateExternalContent() {
        if (noUpdateRequired) {
            return;
        }

        String videoUrl = getVideoUrl();
        if (videoUrl != null) {
            externalContent = new ExternalContent();
            externalContent.setUrl(videoUrl);
            externalContent.getResponse();
        } else {
            externalContent = null;
        }

        noUpdateRequired = true;
    }
}
