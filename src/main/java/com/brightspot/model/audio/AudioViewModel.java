package com.brightspot.model.audio;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.model.audio.AudioView;

public class AudioViewModel extends AbstractViewModel<Audio> implements AudioView {

    @Override
    public Object getName() {
        return model.getName();
    }

    @Override
    public Object getThumbnail() {
        return createView(ImageView.class, model.getPreviewImage());
    }

    @Override
    public Object getAudioPlayer() {
        return buildObjectView(model.getSource());
    }
}
