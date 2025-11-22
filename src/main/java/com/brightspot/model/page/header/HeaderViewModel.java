package com.brightspot.model.page.header;

import java.util.Collection;
import java.util.stream.Collectors;

import com.brightspot.integration.IntegrationSiteSettings;
import com.brightspot.integration.TagManager;
import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.base.page.PageNavigationView;
import com.brightspot.view.base.util.ImageView;
import com.brightspot.view.model.page.header.HeaderView;
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
    public Object getPageNavigation() {
        return createView(PageNavigationView.class, model);
    }

    @Override
    public Collection<?> getTagManager() {
        return IntegrationSiteSettings.get(getCurrentSite(), IntegrationSiteSettings::getExtraHeadItems).stream()
            .filter(TagManager.class::isInstance)
            .map(tagManager -> buildObjectView(TagManager.BODY_VIEW_TYPE, tagManager.unwrap()))
            .collect(Collectors.toList());
    }
}
