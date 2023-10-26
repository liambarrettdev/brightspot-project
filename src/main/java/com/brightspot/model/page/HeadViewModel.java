package com.brightspot.model.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.page.ExternalScriptView;
import com.brightspot.view.base.page.ExternalStylesheetView;
import com.brightspot.view.model.page.HeadView;
import com.psddev.cms.db.Directory;
import com.psddev.cms.db.ElFunctionUtils;
import com.psddev.cms.db.Seo;
import com.psddev.dari.util.StringUtils;

public class HeadViewModel extends AbstractViewModel<AbstractPage> implements HeadView {

    @Override
    public Object getTitle() {
        return model.as(Seo.ObjectModification.class).findTitle();
    }

    @Override
    public Object getDescription() {
        return model.as(Seo.ObjectModification.class).findDescription();
    }

    @Override
    public Object getCanonicalLink() {
        return model.as(Directory.ObjectModification.class).getSitePermalink(getSite());
    }

    @Override
    public Collection<?> getKeywords() {
        return model.as(Seo.ObjectModification.class).findKeywords();
    }

    @Override
    public Collection<?> getExtraItems() {
        List<Object> items = new ArrayList<>();

        String scriptPath = ElFunctionUtils.resource("/All.min.js");
        if (!StringUtils.isBlank(scriptPath)) {
            items.add(new ExternalScriptView.Builder()
                .type("text/javascript")
                .async(false)
                .src(scriptPath)
                .build());
        }

        String stylePath = ElFunctionUtils.resource("/All.min.css");
        if (!StringUtils.isBlank(stylePath)) {
            items.add(new ExternalStylesheetView.Builder()
                .rel("stylesheet")
                .type("text/css")
                .media("all")
                .href(stylePath)
                .build());
        }

        return items;
    }
}
