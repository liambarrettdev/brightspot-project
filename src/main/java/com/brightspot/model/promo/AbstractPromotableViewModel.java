package com.brightspot.model.promo;

import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.utils.LocalizationUtils;
import com.brightspot.utils.StateUtils;
import com.brightspot.view.base.util.ImageView;

public abstract class AbstractPromotableViewModel extends AbstractViewModel<Promotable> {

    private static final String DEFAULT_CTA_TEXT = "Read More";

    public Object getTitle() {
        return model.getPromoTitle();
    }

    public Object getMedia() {
        return createView(ImageView.class, StateUtils.resolve(model.getPromoImage()));
    }

    public Object getByline() {
        return model.getPromotableAuthor();
    }

    public Object getDate() {
        return Optional.ofNullable(model.getPromotableDate())
            .map(date -> LocalizationUtils.localizeDate(date, getCurrentSite(), model.getPromotableDateFormat()))
            .orElse(null);
    }

    public Object getDescription() {
        return model.getPromoDescription();
    }

    public Object getDuration() {
        return model.getPromotableDuration(getCurrentSite());
    }

    public Object getCtaUrl() {
        return model.getPromotableUrl(getCurrentSite());
    }

    public Object getCtaText() {
        return LocalizationUtils.currentSiteText(model, getCurrentSite(), "ctaText", DEFAULT_CTA_TEXT);
    }

    public Object getType() {
        return model.getPromotableType();
    }
}
