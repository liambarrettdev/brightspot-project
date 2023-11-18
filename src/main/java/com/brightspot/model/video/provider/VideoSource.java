package com.brightspot.model.video.provider;

import com.brightspot.model.video.metadata.VideoMetadata;
import com.brightspot.tool.ViewWrapper;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.StorageItem;

@Recordable.Embedded
public abstract class VideoSource extends Record implements ViewWrapper {

    public static final String INTERNAL_NAME = "com.brightspot.model.video.provider.VideoSource";

    public abstract String getVideoId();

    public abstract Long getVideoDuration();

    public abstract Integer getVideoHeight();

    public abstract Integer getVideoWidth();

    private transient VideoMetadata videoMetadata;

    public VideoMetadata getVideoMetadata() {
        return videoMetadata;
    }

    public void setVideoMetadata(VideoMetadata videoMetadata) {
        this.videoMetadata = videoMetadata;
    }

    protected String getVideoTitleFallback() {
        return null;
    }

    protected String getVideoDescriptionFallback() {
        return null;
    }

    public StorageItem getVideoThumbnailFallback() {
        return null;
    }
}
