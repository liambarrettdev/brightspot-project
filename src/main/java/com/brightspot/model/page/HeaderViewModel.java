package com.brightspot.model.page;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.model.navigation.NavigationItemView;
import com.brightspot.view.model.page.HeaderView;
import com.psddev.cms.view.ViewBinding;

@ViewBinding(value = HeaderViewModel.class)
public class HeaderViewModel extends AbstractViewModel<Header> implements HeaderView {

    @Override
    public Object getText() {
        return model.getText();
    }

    @Override
    public Object getImage() {
        return createView(ImageView.class, model.getImage());
    }

    @Override
    public Collection<?> getItems() {
        return model.getNavigationItems().stream()
            .map(navItem -> createView(NavigationItemView.class, navItem))
            .collect(Collectors.toList());
    }
}
