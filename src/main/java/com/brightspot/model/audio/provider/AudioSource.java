package com.brightspot.model.audio.provider;

import com.brightspot.model.audio.metadata.AudioMetadata;
import com.brightspot.tool.ViewWrapper;
import com.psddev.cms.db.ElFunctionUtils;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.StorageItem;

@Recordable.Embedded
public abstract class AudioSource extends Record implements ViewWrapper {

    public static final String INTERNAL_NAME = "com.brightspot.model.audio.provider.AudioSource";

    private static final String DEFAULT_THUMBNAIL = "/assets/placeholders/audio.png";

    public abstract String getAudioId();

    public abstract Long getAudioDuration();

    private transient AudioMetadata audioMetadata;

    public AudioMetadata getAudioMetadata() {
        return audioMetadata;
    }

    public void setAudioMetadata(AudioMetadata audioMetadata) {
        this.audioMetadata = audioMetadata;
    }

    public String getAudioTitleFallback() {
        return null;
    }

    public String getAudioDescriptionFallback() {
        return null;
    }

    public StorageItem getAudioThumbnailFallback() {
        String url = ElFunctionUtils.resource(DEFAULT_THUMBNAIL);
        return StorageItem.Static.createUrl(url);
    }
}
