package com.brightspot.model.promo;

import java.util.Optional;

import com.brightspot.model.image.Image;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("Internal Link")
public class InternalLinkPromo extends Record implements Promo {

    @Required
    private Promotable promotable;

    public Promotable getPromotable() {
        return promotable;
    }

    public void setPromotable(Promotable promotable) {
        this.promotable = promotable;
    }

    @Override
    public String getTitle() {
        return Optional.ofNullable(promotable)
            .map(Promotable::getPromoTitle)
            .orElse(null);
    }

    @Override
    public String getDescription() {
        return Optional.ofNullable(promotable)
            .map(Promotable::getPromoDescription)
            .orElse(null);
    }

    @Override
    public Image getImage() {
        return Optional.ofNullable(promotable)
            .map(Promotable::getPromoImage)
            .orElse(null);
    }

    @Override
    public String getUrl(Site site) {
        return Optional.ofNullable(promotable)
            .map(item -> item.getPromotableUrl(site))
            .orElse(null);
    }
}
