package com.brightspot.model.rte.enhancement.image;

import java.util.Optional;

import com.brightspot.model.rte.enhancement.Alignable;
import com.brightspot.view.base.EnhancementView;
import com.brightspot.view.base.util.ImageView;
import com.psddev.cms.view.ViewModel;

public class ImageEnhancementViewModel extends ViewModel<ImageEnhancement> implements EnhancementView {

    @Override
    public Object getAlignment() {
        return Optional.ofNullable(model.asAlignableData())
            .map(Alignable.Data::getAlignment)
            .map(Alignable.Data.Alignment::getName)
            .orElse(null);
    }

    @Override
    public Object getContent() {
        return createView(ImageView.class, model.getImage());
    }
}
