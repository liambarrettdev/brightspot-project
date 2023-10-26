package com.brightspot.model.gallery;

import com.brightspot.project.view.base.util.ImageView;
import com.brightspot.project.view.model.gallery.SlideView;
import com.psddev.cms.view.ViewModel;

public class SlideViewModel extends ViewModel<Slide> implements SlideView {

    @Override
    public Object getMedia() {
        return createView(ImageView.class, model.getImage());
    }

    @Override
    public Object getTitle() {
        return model.getTitle();
    }

    @Override
    public Object getDescription() {
        return model.getDescription();
    }

    @Override
    public Object getAttribution() {
        return model.getAttribution();
    }
}
