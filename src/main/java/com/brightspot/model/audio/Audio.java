package com.brightspot.model.audio;

import java.util.Optional;

import com.brightspot.model.audio.metadata.AudioMetadata;
import com.brightspot.model.audio.provider.AudioSource;
import com.brightspot.model.image.Image;
import com.brightspot.model.media.AbstractMediaContent;
import com.brightspot.tool.HasImagePreview;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@ToolUi.IconName(Audio.ICON_NAME)
@ViewBinding(value = AudioViewModel.class)
public class Audio extends AbstractMediaContent implements
    AudioMetadata,
    HasImagePreview {

    public static final String ICON_NAME = "music_note";

    private static final String TAB_OVERRIDES = "Overrides";
    private static final String AUDIO_PREDICATE = "groups = " + AudioSource.INTERNAL_NAME
        + " && internalName != " + AudioSource.INTERNAL_NAME
        + " && (cms.ui.hidden = false || cms.ui.hidden = missing)"
        + " && isAbstract = false";

    private String name;

    @ToolUi.NoteHtml("<span data-dynamic-html='${content.asTimedContentData().getDurationLabel()}'></span>")
    private AudioSource source;

    @Recordable.Embedded
    @ToolUi.Tab(TAB_OVERRIDES)
    @ToolUi.NoteHtml("<span data-dynamic-html='${content.getThumbnailPlaceholderHtml()}'></span>")
    private Image thumbnail;

    @Recordable.Indexed
    @Recordable.Where(AUDIO_PREDICATE)
    @ToolUi.Hidden
    @ToolUi.DropDown
    @ToolUi.Filterable
    public ObjectType audioType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public AudioSource getSource() {
        return source;
    }

    public void setSource(AudioSource source) {
        this.source = source;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    // -- Overrides -- //

    @Override
    public void beforeSave() {
        super.beforeSave();

        Optional.ofNullable(getSource()).ifPresent(source -> {
            source.setAudioMetadata(this);
            audioType = source.getState().getType();
        });
    }

    @Override
    public Image getPreviewImage() {
        if (thumbnail == null) {
            return Optional.ofNullable(getSource())
                .map(AudioSource::getAudioThumbnailFallback)
                .map(Image::createImage)
                .orElse(null);
        }

        return thumbnail;
    }

    // AudioMetadata

    @Override
    public Long getDuration() {
        return Optional.ofNullable(getSource())
            .map(AudioSource::getAudioDuration)
            .orElse(null);
    }

    // -- Helper Methods -- //

    public String getThumbnailPlaceholderHtml() {
        return ObjectUtils.isBlank(thumbnail)
            ? writePreviewImageHtml(getPreviewImage())
            : null;
    }
}
