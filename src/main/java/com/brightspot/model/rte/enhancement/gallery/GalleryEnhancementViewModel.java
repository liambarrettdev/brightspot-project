package com.brightspot.model.rte.enhancement.gallery;

import java.util.Optional;

import com.brightspot.model.rte.enhancement.Alignable;
import com.brightspot.project.view.base.EnhancementView;
import com.brightspot.project.view.model.gallery.CarouselView;
import com.psddev.cms.view.ViewModel;

public class GalleryEnhancementViewModel extends ViewModel<GalleryEnhancement> implements EnhancementView {

    @Override
    public Object getAlignment() {
        return Optional.ofNullable(model.asAlignableData())
            .map(Alignable.Data::getAlignment)
            .map(Alignable.Data.Alignment::getName)
            .orElse(null);
    }

    @Override
    public Object getContent() {
        return createView(CarouselView.class, model.getGallery());
    }
}
