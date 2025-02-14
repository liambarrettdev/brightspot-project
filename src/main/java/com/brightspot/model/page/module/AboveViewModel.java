package com.brightspot.model.page.module;

import java.util.List;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.tool.CustomSiteSettings;
import com.brightspot.view.model.page.module.AboveView;
import com.psddev.dari.util.ObjectUtils;

public class AboveViewModel extends AbstractViewModel<AbstractPage> implements AboveView {

    List<AbstractModule> modules;

    @Override
    protected boolean shouldCreate() {
        List<AbstractModule> siteModules = CustomSiteSettings.get(getCurrentSite(), CustomSiteSettings::getAbove);
        modules = siteModules.isEmpty() ? model.getAbove() : siteModules;
        return !ObjectUtils.isBlank(modules);
    }

    @Override
    public Object getContent() {
        return buildConcatenatedView(buildModuleViews(modules));
    }
}
