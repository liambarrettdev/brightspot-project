package com.brightspot.model.navigation;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.navigation.NavigationItemView;

public class NavigationItemViewModel extends AbstractViewModel<NavigationItem> implements NavigationItemView {

    @Override
    public Object getCta() {
        return model.getCtaUrl(getSite());
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
}
