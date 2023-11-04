package com.brightspot.model.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.hierarchy.Hierarchical;
import com.brightspot.model.link.ExternalLink;
import com.brightspot.model.link.InternalLink;
import com.brightspot.model.link.Link;
import com.brightspot.model.link.Linkable;
import com.brightspot.tool.CustomSiteSettings;
import com.brightspot.view.base.util.LinkView;
import com.brightspot.view.model.page.FooterView;
import com.brightspot.view.model.page.HeadView;
import com.brightspot.view.model.page.HeaderView;
import com.brightspot.view.model.page.PageView;
import org.apache.commons.collections4.CollectionUtils;

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
        return Optional.ofNullable(CustomSiteSettings.get(getSite(), CustomSiteSettings::getHeader))
            .map(header -> createView(HeaderView.class, header))
            .orElse(null);
    }

    @Override
    public Collection<?> getBreadcrumbs() {
        List<Link> breadcrumbs = new ArrayList<>(Optional.of(model)
            .filter(Hierarchical.class::isInstance)
            .map(Hierarchical.class::cast)
            .map(hierarchy -> hierarchy.getBreadcrumbs().stream()
                .filter(Linkable.class::isInstance)
                .map(Linkable.class::cast)
                .map(InternalLink::create)
                .collect(Collectors.toList()))
            .orElse(new ArrayList<>()));

        // check if breadcrumbs configured for this content type
        if (CollectionUtils.isEmpty(breadcrumbs)) {
            return null;
        }

        // add current page as read-only node
        Optional.of(getMainContent())
            .filter(Linkable.class::isInstance)
            .map(Linkable.class::cast)
            .map(Linkable::getLinkableText)
            .map(text -> ExternalLink.create(text, null))
            .ifPresent(breadcrumbs::add);

        return breadcrumbs.stream()
            .map(breadcrumb -> createView(LinkView.class, breadcrumb))
            .collect(Collectors.toList());
    }

    @Override
    public Object getContent() {
        return createView(MAIN_CONTENT_VIEW, model);
    }

    @Override
    public Object getFooter() {
        return Optional.ofNullable(CustomSiteSettings.get(getSite(), CustomSiteSettings::getFooter))
            .map(footer -> createView(FooterView.class, footer))
            .orElse(null);
    }

    @Override
    public Object getType() {
        return model.getPageType();
    }
}
