package com.brightspot.model.audio.provider;

import com.brightspot.model.AbstractViewModel;

public abstract class AbstractAudioPlayerViewModel<M extends AudioSource> extends AbstractViewModel<M> {

    public Object getAudioId() {
        return model.getAudioId();
    }

    public Object getAudioTitle() {
        return model.getAudioTitleFallback();
    }
}
