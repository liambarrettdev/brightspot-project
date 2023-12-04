package com.brightspot.model.promo;

import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.utils.LocalizationUtils;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.model.promo.PromoModuleView;
import com.psddev.cms.view.ViewResponse;

public class PromoModuleViewModel extends AbstractViewModel<PromoModule> implements PromoModuleView {

    private transient Promotable promo;

    @Override
    protected void onCreate(ViewResponse response) {
        super.onCreate(response);

        promo = Optional.of(model)
            .map(PromoModule::getPromo)
            .filter(InternalLinkPromo.class::isInstance)
            .map(InternalLinkPromo.class::cast)
            .map(InternalLinkPromo::getPromotable)
            .orElse(null);
    }

    @Override
    public Object getTitle() {
        return model.getTitle();
    }

    @Override
    public Object getMedia() {
        return Optional.ofNullable(model.getImage())
            .map(image -> createView(ImageView.class, image))
            .orElse(null);
    }

    @Override
    public Object getByline() {
        return Optional.ofNullable(promo)
            .map(Promotable::getPromotableAuthor)
            .orElse(null);
    }

    @Override
    public Object getDate() {
        return Optional.ofNullable(promo)
            .map(Promotable::getPromotableDate)
            .map(date -> LocalizationUtils.localizeDate(date, getCurrentSite(), DEFAULT_DATE_FORMAT))
            .orElse(null);
    }

    @Override
    public Object getDescription() {
        return model.getDescription();
    }

    @Override
    public Object getDuration() {
        return Optional.ofNullable(promo)
            .map(promotable -> promotable.getPromotableDuration(getCurrentSite()))
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

    @Override
    public Object getType() {
        return Optional.ofNullable(promo)
            .map(Promotable::getPromotableType)
            .orElse(null);
    }
}
