package com.brightspot.model.page.module;

import java.util.List;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.tool.CustomSiteSettings;
import com.brightspot.view.model.page.module.AboveView;

public class AboveViewModel extends AbstractViewModel<AbstractPage> implements AboveView {

    @Override
    public Object getContent() {
        List<AbstractModule> siteModules = CustomSiteSettings.get(getCurrentSite(), CustomSiteSettings::getAbove);
        return buildModuleViews(siteModules.isEmpty() ? model.getAbove() : siteModules);
    }
}
