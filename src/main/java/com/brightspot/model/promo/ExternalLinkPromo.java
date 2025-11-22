package com.brightspot.model.promo;

import com.brightspot.tool.field.annotation.Url;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("External Link")
public class ExternalLinkPromo extends Record implements Promo {

    @Url
    @Recordable.Required
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUrl(Site site) {
        return getUrl();
    }
}
