package com.brightspot.model.page;

import java.util.Locale;
import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.tool.CustomSiteSettings;
import com.brightspot.project.view.model.page.FooterView;
import com.brightspot.project.view.model.page.HeadView;
import com.brightspot.project.view.model.page.HeaderView;
import com.brightspot.project.view.model.page.PageView;

public class AbstractPageViewModel<M extends AbstractPage> extends AbstractViewModel<M> implements PageView {

    public static final String MAIN_CONTENT_VIEW = "main";

    @Override
    public Object getLocale() {
        Locale locale = CustomSiteSettings.get(getSite(), CustomSiteSettings::getLocale);
        return Optional.ofNullable(locale)
            .map(Locale::getLanguage)
            .orElse(null);
    }

    @Override
    public Object getHead() {
        return createView(HeadView.class, model);
    }

    @Override
    public Object getHeader() {
        return createView(HeaderView.class, model);
    }

    @Override
    public Object getHeadline() {
        return model.getDisplayName();
    }

    @Override
    public Object getContent() {
        return createView(MAIN_CONTENT_VIEW, model);
    }

    @Override
    public Object getFooter() {
        return createView(FooterView.class, model);
    }

    @Override
    public Object getType() {
        return model.getPageType();
    }
}
