package com.brightspot.model.video.provider;

import java.util.UUID;

import com.brightspot.model.AbstractViewModel;

public abstract class AbstractVideoPlayerViewModel<M extends VideoSource> extends AbstractViewModel<M> {

    public Object getPlayerId() {
        return "f" + UUID.randomUUID().toString().replace("-", "");
    }

    public Object getVideoId() {
        return model.getVideoId();
    }

    public Object getVideoTitle() {
        return model.getVideoTitleFallback();
    }

    public Boolean getAutoplay() {
        return false;
    }

    public Boolean getMuted() {
        return false;
    }

}
