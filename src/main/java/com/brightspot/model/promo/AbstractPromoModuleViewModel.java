package com.brightspot.model.promo;

import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.utils.LocalizationUtils;
import com.brightspot.view.base.util.ImageView;
import com.psddev.cms.view.ViewResponse;

public abstract class AbstractPromoModuleViewModel extends AbstractViewModel<PromoModule> {

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

    public Object getTitle() {
        return model.getTitle();
    }

    public Object getMedia() {
        return Optional.ofNullable(model.getImage())
            .map(image -> createView(ImageView.class, image))
            .orElse(null);
    }

    public Object getByline() {
        return Optional.ofNullable(promo)
            .map(Promotable::getPromotableAuthor)
            .orElse(null);
    }

    public Object getDate() {
        return Optional.ofNullable(promo)
            .map(Promotable::getPromotableDate)
            .map(date -> LocalizationUtils.localizeDate(date, getCurrentSite(), promo.getPromotableDateFormat()))
            .orElse(null);
    }

    public Object getDescription() {
        return model.getDescription();
    }

    public Object getDuration() {
        return Optional.ofNullable(promo)
            .map(promotable -> promotable.getPromotableDuration(getCurrentSite()))
            .orElse(null);
    }

    public Object getCtaUrl() {
        return Optional.ofNullable(model.getPromo())
            .map(promo -> promo.getUrl(getCurrentSite()))
            .orElse(null);
    }

    public Object getCtaText() {
        return model.getCtaText();
    }

    public Object getType() {
        return Optional.ofNullable(promo)
            .map(Promotable::getPromotableType)
            .orElse(null);
    }
}
