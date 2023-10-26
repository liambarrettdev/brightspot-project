package com.brightspot.model.page;

import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.page.FooterView;
import com.brightspot.tool.CustomSiteSettings;

public class FooterViewModel extends AbstractViewModel<AbstractPage> implements FooterView {

    @Override
    public Object getCopyright() {
        return Optional.ofNullable(getSite())
            .map(s -> CustomSiteSettings.get(s, CustomSiteSettings::getCopyright))
            .map(this::buildRichTextView)
            .orElse(null);
    }
}
