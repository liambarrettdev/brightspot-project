package com.brightspot.model.rte.enhancement.audio;

import java.util.Optional;

import com.brightspot.model.rte.enhancement.Alignable;
import com.brightspot.view.base.EnhancementView;
import com.brightspot.view.model.audio.AudioView;
import com.psddev.cms.view.ViewModel;

public class AudioEnhancementViewModel extends ViewModel<AudioEnhancement> implements EnhancementView {

    @Override
    public Object getAlignment() {
        return Optional.ofNullable(model.asAlignableData())
            .map(Alignable.Data::getAlignment)
            .map(Alignable.Data.Alignment::getName)
            .orElse(null);
    }

    @Override
    public Object getContent() {
        return createView(AudioView.class, model.getAudio());
    }
}
