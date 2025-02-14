package com.brightspot.model.rte.enhancement.video;

import java.util.Optional;

import com.brightspot.model.rte.enhancement.Alignable;
import com.brightspot.view.base.EnhancementView;
import com.brightspot.view.model.video.VideoView;
import com.psddev.cms.view.ViewModel;

public class VideoEnhancementViewModel extends ViewModel<VideoEnhancement> implements EnhancementView {

    @Override
    public Object getAlignment() {
        return Optional.ofNullable(model.asAlignableData())
            .map(Alignable.Data::getAlignment)
            .map(Alignable.Data.Alignment::getName)
            .orElse(null);
    }

    @Override
    public Object getContent() {
        return createView(VideoView.class, model.getVideo());
    }
}
