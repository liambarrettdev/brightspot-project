package com.brightspot.model.video;

import java.util.Optional;

import com.brightspot.model.image.Image;
import com.brightspot.model.media.AbstractMediaContent;
import com.brightspot.model.video.metadata.VideoMetadata;
import com.brightspot.model.video.provider.VideoSource;
import com.brightspot.tool.HasImagePreview;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.util.ObjectUtils;

@ToolUi.IconName(Video.ICON_NAME)
@ViewBinding(value = VideoViewModel.class)
public class Video extends AbstractMediaContent implements
    HasImagePreview,
    VideoMetadata {

    public static final String ICON_NAME = "slideshow";

    private static final String TAB_OVERRIDES = "Overrides";

    private String name;

    @Indexed
    private VideoSource source;

    @Embedded
    @ToolUi.Tab(TAB_OVERRIDES)
    @ToolUi.NoteHtml("<span data-dynamic-html='${content.getThumbnailPlaceholderHtml()}'></span>")
    private Image thumbnail;

    @Indexed
    @ToolUi.Hidden
    @ToolUi.Filterable
    @ToolUi.DropDown
    @Where("groups = " + VideoSource.INTERNAL_NAME
        + " && internalName != " + VideoSource.INTERNAL_NAME
        + " && (cms.ui.hidden = false || cms.ui.hidden = missing)"
        + " && isAbstract = false")
    public ObjectType videoType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VideoSource getSource() {
        return source;
    }

    public void setSource(VideoSource source) {
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
    protected void beforeSave() {
        super.beforeSave();

        Optional.ofNullable(getSource()).ifPresent(source -> {
            source.setVideoMetadata(this);
            videoType = source.getState().getType();
        });
    }

    @Override
    public Image getPreviewImage() {
        if (thumbnail == null) {
            return Optional.ofNullable(getSource())
                .map(VideoSource::getVideoThumbnailFallback)
                .map(Image::createImage)
                .orElse(null);
        }

        return thumbnail;
    }

    // VideoMetadata

    @Override
    public Long getDuration() {
        return Optional.ofNullable(getSource())
            .map(VideoSource::getVideoDuration)
            .orElse(null);
    }

    // -- Helper Methods -- //

    public String getThumbnailPlaceholderHtml() {
        return ObjectUtils.isBlank(thumbnail)
            ? writePreviewImageHtml(getPreviewImage())
            : null;
    }
}
