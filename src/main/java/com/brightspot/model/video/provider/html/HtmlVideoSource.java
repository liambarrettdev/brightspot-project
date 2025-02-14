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
    private HtmlVideoFileWrapper content;

    public HtmlVideoFileWrapper getContent() {
        return content;
    }

    public void setContent(HtmlVideoFileWrapper content) {
        this.content = content;
    }

    // -- Overrides -- //

    @Override
    public String getVideoId() {
        return Optional.ofNullable(getContent())
            .map(HtmlVideoFileWrapper::getFile)
            .map(file -> file.getStorage() + ":" + file.getPath())
            .orElse(null);
    }

    @Override
    public Long getVideoDuration() {
        return Optional.ofNullable(getContent())
            .map(HtmlVideoFileWrapper::getDuration)
            .orElse(null);
    }

    @Override
    public Integer getVideoHeight() {
        return Optional.ofNullable(getContent())
            .map(HtmlVideoFileWrapper::getHeight)
            .orElse(null);
    }

    @Override
    public Integer getVideoWidth() {
        return Optional.ofNullable(getContent())
            .map(HtmlVideoFileWrapper::getWidth)
            .orElse(null);
    }

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }
}
