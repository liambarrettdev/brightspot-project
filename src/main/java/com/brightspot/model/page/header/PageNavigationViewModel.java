package com.brightspot.model.page.header;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.view.base.page.PageNavigationView;
import com.brightspot.view.model.navigation.NavigationItemView;
import com.psddev.cms.view.ViewModel;

public class PageNavigationViewModel extends ViewModel<Header> implements PageNavigationView {

    @Override
    public Collection<?> getItems() {
        return model.getNavigationItems().stream()
            .map(navItem -> createView(NavigationItemView.class, navItem))
            .collect(Collectors.toList());
    }
}
