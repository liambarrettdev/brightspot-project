package com.brightspot.model.gallery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.model.gallery.GalleryModuleView;
import com.brightspot.view.model.gallery.SlideView;

public class GalleryModuleViewModel extends AbstractViewModel<GalleryModule> implements GalleryModuleView {

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
        List<SlideView> views = new ArrayList<>();

        int total = model.getSlides().size();
        for (int i = 0; i < total; i++) {
            int index = i + 1;
            Slide slide = model.getSlides().get(i);
            views.add(buildSlideView(slide, index, total));
        }

        return views;
    }

    private SlideView buildSlideView(Slide slide, int index, int total) {
        if (slide == null) {
            return null;
        }
        return new SlideView.Builder()
            .media(createView(ImageView.class, slide.getImage()))
            .title(slide.getTitle())
            .description(slide.getDescription())
            .attribution(slide.getAttribution())
            .index(index)
            .total(total)
            .build();
    }
}
