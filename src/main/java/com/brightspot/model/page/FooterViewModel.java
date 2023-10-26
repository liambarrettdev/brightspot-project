package com.brightspot.model.page;

import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.tool.CustomSiteSettings;
import com.brightspot.project.view.model.page.FooterView;

public class FooterViewModel extends AbstractViewModel<AbstractPage> implements FooterView {

    @Override
    public Object getCopyright() {
        return Optional.ofNullable(getSite())
            .map(s -> CustomSiteSettings.get(s, CustomSiteSettings::getCopyright))
            .map(this::buildRichTextView)
            .orElse(null);
    }
}
