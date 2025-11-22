package com.brightspot.model.page.module;

import java.util.List;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.tool.CustomSiteSettings;
import com.brightspot.view.model.page.module.AsideView;
import com.psddev.dari.util.ObjectUtils;

public class AsideViewModel extends AbstractViewModel<AbstractPage> implements AsideView {

    List<AbstractModule> modules;

    @Override
    protected boolean shouldCreate() {
        List<AbstractModule> siteModules = CustomSiteSettings.get(getCurrentSite(), CustomSiteSettings::getAside);
        modules = siteModules.isEmpty() ? model.getAside() : siteModules;
        return !ObjectUtils.isBlank(modules);
    }

    @Override
    public Object getContent() {
        return buildConcatenatedView(buildModuleViews(modules));
    }
}
