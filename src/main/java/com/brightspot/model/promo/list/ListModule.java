package com.brightspot.model.promo.list;

import com.brightspot.model.list.ListType;
import com.brightspot.model.module.AbstractModule;
import com.brightspot.model.module.ShareableModule;
import com.brightspot.model.promo.list.type.CuratedListType;
import com.brightspot.tool.rte.BasicRichTextToolbar;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("Promo List")
@ViewBinding(value = ListModuleViewModel.class, types = ListModule.VIEW_CLASS)
public class ListModule extends AbstractModule implements ShareableModule {

    protected static final String VIEW_CLASS = "promo-module";

    private String title;

    @ToolUi.RichText(toolbar = BasicRichTextToolbar.class)
    private String description;

    private ListType type = new CuratedListType();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ListType getType() {
        return type;
    }

    public void setType(ListType type) {
        this.type = type;
    }

    // -- Overrides -- //

    @Override
    public String getViewType() {
        return VIEW_CLASS;
    }

    @Override
    public String getLabel() {
        return getTitle();
    }
}
