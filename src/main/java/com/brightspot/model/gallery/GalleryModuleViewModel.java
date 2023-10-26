package com.brightspot.model.gallery;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.gallery.CarouselView;
import com.brightspot.view.model.gallery.SlideView;

public class GalleryModuleViewModel extends AbstractViewModel<GalleryModule> implements CarouselView {

    @Override
    public Object getTitle() {
        return model.getName();
    }

    @Override
    public Object getDescription() {
        return buildRichTextView(model.getDescription());
    }

    @Override
    public Collection<?> getSlides() {
        return model.getSlides().stream()
            .map(slide -> createView(SlideView.class, slide))
            .collect(Collectors.toList());
    }
}
