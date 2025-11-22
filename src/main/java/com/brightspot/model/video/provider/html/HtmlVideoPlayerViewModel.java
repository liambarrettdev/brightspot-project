package com.brightspot.model.video.provider.html;

import java.util.Optional;

import com.brightspot.model.video.provider.AbstractVideoPlayerViewModel;
import com.brightspot.view.model.video.html.HtmlVideoPlayerSourceView;
import com.brightspot.view.model.video.html.HtmlVideoPlayerView;

public class HtmlVideoPlayerViewModel extends AbstractVideoPlayerViewModel<HtmlVideoSource> implements HtmlVideoPlayerView {

    @Override
    public Object getSource() {
        return Optional.ofNullable(model.getContent())
            .map(HtmlVideoFileWrapper::getFile)
            .map(file -> new HtmlVideoPlayerSourceView.Builder()
                .src(file.getPublicUrl())
                .type(file.getContentType())
                .build())
            .orElse(null);
    }
}
