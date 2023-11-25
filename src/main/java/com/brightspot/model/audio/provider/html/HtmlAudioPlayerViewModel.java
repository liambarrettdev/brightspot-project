package com.brightspot.model.audio.provider.html;

import java.util.Optional;

import com.brightspot.model.audio.provider.AbstractAudioPlayerViewModel;
import com.brightspot.view.model.audio.html.HtmlAudioPlayerSourceView;
import com.brightspot.view.model.audio.html.HtmlAudioPlayerView;

public class HtmlAudioPlayerViewModel extends AbstractAudioPlayerViewModel<HtmlAudioSource> implements HtmlAudioPlayerView {

    @Override
    public Object getSource() {
        return Optional.ofNullable(model.getContent())
            .map(HtmlFileWrapper::getFile)
            .map(file -> new HtmlAudioPlayerSourceView.Builder()
                .src(file.getPublicUrl())
                .type(file.getContentType())
                .build())
            .orElse(null);
    }
}
