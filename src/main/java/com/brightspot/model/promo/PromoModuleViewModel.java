package com.brightspot.model.promo;

import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.model.promo.PromoModuleView;

public class PromoModuleViewModel extends AbstractViewModel<PromoModule> implements PromoModuleView {

    @Override
    public Object getTitle() {
        return model.getTitle();
    }

    @Override
    public Object getDescription() {
        return model.getDescription();
    }

    @Override
    public Object getMedia() {
        return Optional.ofNullable(model.getImage())
            .map(image -> createView(ImageView.class, image))
            .orElse(null);
    }

    @Override
    public Object getCta() {
        return Optional.ofNullable(model.getPromo())
            .map(promo -> promo.getUrl(getCurrentSite()))
            .orElse(null);
    }

    @Override
    public Object getCtaText() {
        return model.getCtaText();
    }
}
