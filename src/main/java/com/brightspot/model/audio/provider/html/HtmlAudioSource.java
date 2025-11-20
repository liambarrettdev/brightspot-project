package com.brightspot.model.audio.provider.html;

import java.util.Optional;

import com.brightspot.model.audio.provider.AudioSource;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("HTML5")
@ViewBinding(value = HtmlAudioPlayerViewModel.class, types = HtmlAudioSource.VIEW_TYPE)
public class HtmlAudioSource extends AudioSource {

    protected static final String VIEW_TYPE = "html-audio";

    @Recordable.Required
    private HtmlAudioFileWrapper content;

    public HtmlAudioFileWrapper getContent() {
        return content;
    }

    public void setContent(HtmlAudioFileWrapper content) {
        this.content = content;
    }

    // -- Overrides -- //

    @Override
    public String getAudioId() {
        return Optional.ofNullable(getContent())
            .map(HtmlAudioFileWrapper::getFile)
            .map(file -> file.getStorage() + ":" + file.getPath())
            .orElse(null);
    }

    @Override
    public Long getAudioDuration() {
        return Optional.ofNullable(getContent())
            .map(HtmlAudioFileWrapper::getDuration)
            .orElse(null);
    }

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }
}
