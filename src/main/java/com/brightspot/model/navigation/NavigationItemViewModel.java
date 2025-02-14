package com.brightspot.model.navigation;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.navigation.NavigationItemView;
import com.psddev.dari.util.PageContextFilter;

public class NavigationItemViewModel extends AbstractViewModel<NavigationItem> implements NavigationItemView {

    @Override
    public Object getCtaUrl() {
        return model.getCtaUrl(getCurrentSite());
    }

    @Override
    public Object getCtaText() {
        return model.getCtaText();
    }

    @Override
    public Collection<?> getItems() {
        return model.getItems().stream()
            .map(item -> createView(NavigationItemView.class, item))
            .collect(Collectors.toList());
    }

    @Override
    public Boolean getDropdown() {
        return model instanceof NavigationListItem;
    }

    @Override
    public Boolean getActive() {
        return Optional.ofNullable(PageContextFilter.Static.getRequestOrNull())
            .map(HttpServletRequest::getRequestURL)
            .map(StringBuffer::toString)
            .map(url -> {
                String ctaUrl = model.getCtaUrl(getCurrentSite());
                return url.equals(ctaUrl);
            })
            .orElse(false);
    }
}
