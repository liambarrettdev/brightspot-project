package com.brightspot.model.promo;

import com.brightspot.model.image.Image;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.Recordable;

public interface Promo extends Recordable {

    default String getTitle() {
        return null;
    }

    default String getDescription() {
        return null;
    }

    default Image getImage() {
        return null;
    }

    default String getUrl(Site site) {
        return null;
    }
}
