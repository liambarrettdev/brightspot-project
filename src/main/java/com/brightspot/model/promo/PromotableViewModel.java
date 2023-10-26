package com.brightspot.model.promo;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.model.promo.PromoView;
import com.brightspot.utils.LocalizationUtils;
import com.brightspot.utils.StateUtils;

public class PromotableViewModel extends AbstractViewModel<Promotable> implements PromoView {

    public static final String DEFAULT_CTA_TEXT = "Read More";

    @Override
    public Object getTitle() {
        return model.getPromoTitle();
    }

    @Override
    public Object getDescription() {
        return buildRichTextView(model.getPromoDescription());
    }

    @Override
    public Object getMedia() {
        return createView(ImageView.class, StateUtils.resolve(model.getPromoImage()));
    }

    @Override
    public Object getCtaUrl() {
        return model.getPromotableUrl(getSite());
    }

    @Override
    public Object getCtaText() {
        return LocalizationUtils.currentSiteText(model, getSite(), "ctaText", DEFAULT_CTA_TEXT);
    }
}
