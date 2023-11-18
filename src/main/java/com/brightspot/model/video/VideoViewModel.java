package com.brightspot.model.video;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.model.video.VideoView;

public class VideoViewModel extends AbstractViewModel<Video> implements VideoView {

    @Override
    public Object getName() {
        return model.getName();
    }

    @Override
    public Object getThumbnail() {
        return createView(ImageView.class, model.getPreviewImage());
    }

    @Override
    public Object getVideoPlayer() {
        return buildObjectView(model.getSource());
    }
}
