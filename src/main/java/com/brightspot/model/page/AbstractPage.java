package com.brightspot.model.page;

import com.psddev.cms.db.Content;
import com.psddev.cms.db.Directory;
import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.Seo;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.util.ObjectUtils;

@Seo.TitleFields("displayName")
@ViewBinding(value = PageHeadViewModel.class)
@ViewBinding(value = AbstractPageViewModel.class, types = { PageFilter.PAGE_VIEW_TYPE })
public abstract class AbstractPage extends Content implements Directory.Item {

    @DisplayName("Internal Name")
    @ToolUi.Placeholder(dynamicText = "${content.getNameFallback()}", editable = true)
    private String name;

    @Indexed
    @Required
    private String displayName;

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
