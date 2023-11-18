package com.brightspot.model.page.module;

import java.util.List;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.page.AbstractPage;
import com.brightspot.tool.CustomSiteSettings;
import com.brightspot.view.model.page.module.AsideView;

public class AsideViewModel extends AbstractViewModel<AbstractPage> implements AsideView {

    @Override
    public Object getContent() {
        List<AbstractModule> siteModules = CustomSiteSettings.get(getCurrentSite(), CustomSiteSettings::getAside);
        return buildModuleViews(siteModules.isEmpty() ? model.getAside() : siteModules);
    }
}
