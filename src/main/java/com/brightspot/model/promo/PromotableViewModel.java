package com.brightspot.model.promo;

import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.utils.LocalizationUtils;
import com.brightspot.utils.StateUtils;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.model.promo.PromoModuleView;

public class PromotableViewModel extends AbstractViewModel<Promotable> implements PromoModuleView {

    private static final String DEFAULT_CTA_TEXT = "Read More";

    @Override
    public Object getTitle() {
        return model.getPromoTitle();
    }

    @Override
    public Object getMedia() {
        return createView(ImageView.class, StateUtils.resolve(model.getPromoImage()));
    }

    @Override
    public Object getByline() {
        return model.getPromotableAuthor();
    }

    @Override
    public Object getDate() {
        return Optional.ofNullable(model.getPromotableDate())
            .map(date -> LocalizationUtils.localizeDate(date, getCurrentSite(), DEFAULT_DATE_FORMAT))
            .orElse(null);
    }

    @Override
    public Object getDescription() {
        return model.getPromoDescription();
    }

    @Override
    public Object getDuration() {
        return model.getPromotableDuration(getCurrentSite());
    }

    @Override
    public Object getCta() {
        return model.getPromotableUrl(getCurrentSite());
    }

    @Override
    public Object getCtaText() {
        return LocalizationUtils.currentSiteText(model, getCurrentSite(), "ctaText", DEFAULT_CTA_TEXT);
    }

    @Override
    public Object getType() {
        return model.getPromotableType();
    }
}
