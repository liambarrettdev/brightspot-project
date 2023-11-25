package com.brightspot.model.video.provider.html;

import java.util.Optional;

import com.brightspot.model.video.provider.VideoSource;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("HTML5")
@ViewBinding(value = HtmlVideoPlayerViewModel.class, types = HtmlVideoSource.VIEW_TYPE)
public class HtmlVideoSource extends VideoSource {

    protected static final String VIEW_TYPE = "html-video";

    @Required
    private HtmlFileWrapper content;

    public HtmlFileWrapper getContent() {
        return content;
    }

    public void setContent(HtmlFileWrapper content) {
        this.content = content;
    }

    // -- Overrides -- //

    @Override
    public String getVideoId() {
        return Optional.ofNullable(getContent())
            .map(HtmlFileWrapper::getFile)
            .map(file -> file.getStorage() + ":" + file.getPath())
            .orElse(null);
    }

    @Override
    public Long getVideoDuration() {
        return Optional.ofNullable(getContent())
            .map(HtmlFileWrapper::getDuration)
            .map(duration -> duration * 1_000)
            .orElse(null);
    }

    @Override
    public Integer getVideoHeight() {
        return Optional.ofNullable(getContent())
            .map(HtmlFileWrapper::getHeight)
            .orElse(null);
    }

    @Override
    public Integer getVideoWidth() {
        return Optional.ofNullable(getContent())
            .map(HtmlFileWrapper::getWidth)
            .orElse(null);
    }

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }
}
