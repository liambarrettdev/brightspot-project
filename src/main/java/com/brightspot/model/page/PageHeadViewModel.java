package com.brightspot.model.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.brightspot.integration.IntegrationSiteSettings;
import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.page.element.LinkElement;
import com.brightspot.model.page.element.MetaElement;
import com.brightspot.model.page.element.ScriptElement;
import com.brightspot.model.page.element.StylesheetElement;
import com.brightspot.view.base.page.ExternalScriptView;
import com.brightspot.view.base.page.ExternalStylesheetView;
import com.brightspot.view.base.page.LinkView;
import com.brightspot.view.base.page.MetaView;
import com.brightspot.view.model.page.HeadView;
import com.psddev.cms.db.Directory;
import com.psddev.cms.db.ElFunctionUtils;
import com.psddev.cms.db.Seo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class PageHeadViewModel extends AbstractViewModel<AbstractPage> implements HeadView {

    @Override
    public Object getTitle() {
        return model.as(Seo.ObjectModification.class).findTitle();
    }

    @Override
    public Collection<?> getLinkElements() {
        List<Object> items = new ArrayList<>();

        // default links
        String canonicalUrl = model.as(Directory.ObjectModification.class).getSitePermalink(getCurrentSite());
        if (StringUtils.isNotBlank(canonicalUrl)) {
            items.add(new LinkView.Builder()
                .href(canonicalUrl)
                .rel("canonical")
                .build());
        }

        // site-specific links
        items.addAll(IntegrationSiteSettings.get(getCurrentSite(), IntegrationSiteSettings::getCustomHeadElements)
            .stream()
            .map(item -> item.getElements(LinkElement.class))
            .flatMap(elements -> elements.stream().map(this::buildObjectView))
            .collect(Collectors.toList()));

        return items;
    }

    @Override
    public Collection<?> getMetaElements() {
        List<Object> items = new ArrayList<>();

        // default mata tags
        String metaDescription = model.as(Seo.ObjectModification.class).findDescription();
        if (StringUtils.isNotBlank(metaDescription)) {
            items.add(new MetaView.Builder()
                .name("description")
                .content(metaDescription)
                .build());
        }

        Set<String> metaKeywords = model.as(Seo.ObjectModification.class).findKeywords();
        if (CollectionUtils.isNotEmpty(metaKeywords)) {
            items.add(new MetaView.Builder()
                .name("keywords")
                .content(String.join(",", metaKeywords))
                .build());
        }

        // site-specific meta tags
        items.addAll(IntegrationSiteSettings.get(getCurrentSite(), IntegrationSiteSettings::getCustomHeadElements)
            .stream()
            .map(item -> item.getElements(MetaElement.class))
            .flatMap(elements -> elements.stream().map(this::buildObjectView))
            .collect(Collectors.toList()));

        return items;
    }

    @Override
    public Collection<?> getScriptElements() {
        List<Object> items = new ArrayList<>();

        // default scripts
        String scriptPath = ElFunctionUtils.resource("/All.min.js");
        if (StringUtils.isNotBlank(scriptPath)) {
            items.add(new ExternalScriptView.Builder()
                .type("text/javascript")
                .async(false)
                .src(scriptPath)
                .build());
        }

        // site-specific scripts
        items.addAll(IntegrationSiteSettings.get(getCurrentSite(), IntegrationSiteSettings::getCustomHeadElements)
            .stream()
            .map(item -> item.getElements(ScriptElement.class))
            .flatMap(elements -> elements.stream().map(this::buildObjectView))
            .collect(Collectors.toList()));

        return items;
    }

    @Override
    public Collection<?> getStyleElements() {
        List<Object> items = new ArrayList<>();

        // default stylesheets
        String stylePath = ElFunctionUtils.resource("/All.min.css");
        if (StringUtils.isNotBlank(stylePath)) {
            items.add(new ExternalStylesheetView.Builder()
                .rel("stylesheet")
                .type("text/css")
                .media("all")
                .href(stylePath)
                .build());
        }

        // site-specific stylesheets
        items.addAll(IntegrationSiteSettings.get(getCurrentSite(), IntegrationSiteSettings::getCustomHeadElements)
            .stream()
            .map(item -> item.getElements(StylesheetElement.class))
            .flatMap(elements -> elements.stream().map(this::buildObjectView))
            .collect(Collectors.toList()));

        return items;
    }

    @Override
    public Collection<?> getExtraItems() {
        return IntegrationSiteSettings.get(getCurrentSite(), IntegrationSiteSettings::getExtraHeadItems).stream()
            .filter(item -> !item.asHeadItemData().isDisabled())
            .map(this::buildObjectView)
            .collect(Collectors.toList());
    }
}
