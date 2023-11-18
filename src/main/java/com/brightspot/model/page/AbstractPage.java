package com.brightspot.model.page;

import java.util.ArrayList;
import java.util.List;

import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.page.module.AboveViewModel;
import com.brightspot.model.page.module.AsideViewModel;
import com.brightspot.model.page.module.BelowViewModel;
import com.brightspot.model.restricted.Restrictable;
import com.brightspot.tool.CustomSiteSettings;
import com.psddev.cms.db.Content;
import com.psddev.cms.db.Directory;
import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.Seo;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.util.ObjectUtils;

@Seo.TitleFields("displayName")
@ViewBinding(value = AboveViewModel.class)
@ViewBinding(value = AsideViewModel.class)
@ViewBinding(value = BelowViewModel.class)
@ViewBinding(value = PageHeadViewModel.class)
@ViewBinding(value = AbstractPageViewModel.class, types = { PageFilter.PAGE_VIEW_TYPE })
public abstract class AbstractPage extends Content implements
    Directory.Item,
    Restrictable {

    @ToolUi.DisplayName("Internal Name")
    @ToolUi.Placeholder(dynamicText = "${content.getNameFallback()}", editable = true)
    private String name;

    @Indexed
    @Required
    private String displayName;

    @ToolUi.Tab(CustomSiteSettings.TAB_LAYOUT)
    @ToolUi.Note("This will override the default Above in site settings")
    private List<AbstractModule> above;

    @ToolUi.Tab(CustomSiteSettings.TAB_LAYOUT)
    @ToolUi.Note("This will override the default Aside in site settings")
    private List<AbstractModule> aside;

    @ToolUi.Tab(CustomSiteSettings.TAB_LAYOUT)
    @ToolUi.Note("This will override the default Below in site settings")
    private List<AbstractModule> below;

    public String getName() {
        return ObjectUtils.firstNonBlank(name, getNameFallback());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<AbstractModule> getAbove() {
        if (above == null) {
            above = new ArrayList<>();
        }
        return above;
    }

    public void setAbove(List<AbstractModule> above) {
        this.above = above;
    }

    public List<AbstractModule> getAside() {
        if (aside == null) {
            aside = new ArrayList<>();
        }
        return aside;
    }

    public void setAside(List<AbstractModule> aside) {
        this.aside = aside;
    }

    public List<AbstractModule> getBelow() {
        if (below == null) {
            below = new ArrayList<>();
        }
        return below;
    }

    public void setBelow(List<AbstractModule> below) {
        this.below = below;
    }
    // -- Overrides -- //

    @Override
    public String getLabel() {
        return getName();
    }

    // -- Helper Methods -- //

    public String getPageType() {
        return this.getClass().getSimpleName();
    }

    public String getNameFallback() {
        return getDisplayName();
    }
}
